package com.priyanshu.portfolio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestClient;

class GmailContactMailerTest {

    private final JavaMailSender smtp = mock(JavaMailSender.class);

    private final ContactService.Message msg = new ContactService.Message(
            "Ada\r\nBcc: evil@example.com", "ada@example.com", "Let's talk about a senior role.", Instant.now());

    private ContactMailer mailer(MailProperties props) {
        ObjectProvider<JavaMailSender> provider =
                new StaticListableBeanFactory(Map.of("smtp", smtp)).getBeanProvider(JavaMailSender.class);
        return new ContactMailer(props, RestClient.builder(), provider);
    }

    private static MailProperties gmail() {
        return new MailProperties("owner@example.com", "sender@gmail.com", "app-password", "", "");
    }

    @Test
    void sendsFromGmailAccountToOwnerWithReplyToVisitor() {
        ContactMailer mailer = mailer(gmail());

        assertThat(mailer.channel()).isEqualTo(ContactMailer.Channel.GMAIL);
        assertThat(mailer.send(msg)).isTrue();

        ArgumentCaptor<SimpleMailMessage> sent = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(smtp).send(sent.capture());
        SimpleMailMessage mail = sent.getValue();
        assertThat(mail.getFrom()).isEqualTo("Portfolio Contact <sender@gmail.com>");
        assertThat(mail.getTo()).containsExactly("owner@example.com");
        assertThat(mail.getReplyTo()).isEqualTo("ada@example.com");
        assertThat(mail.getSubject()).doesNotContain("\r", "\n");
        assertThat(mail.getText()).contains("Let's talk about a senior role.");
    }

    @Test
    void reportsFailureWhenSmtpFails() {
        doThrow(new MailSendException("Connection timed out")).when(smtp).send(any(SimpleMailMessage.class));

        assertThat(mailer(gmail()).send(msg)).isFalse();
    }

    @Test
    void prefersGmailOverResendWhenBothConfigured() {
        MailProperties both = new MailProperties("owner@example.com", "sender@gmail.com", "app-password", "re_key", "x");
        assertThat(mailer(both).channel()).isEqualTo(ContactMailer.Channel.GMAIL);
    }

    @Test
    void fallsBackToResendWithoutGmailPassword() {
        MailProperties resendOnly = new MailProperties("owner@example.com", "sender@gmail.com", "", "re_key", "x");
        assertThat(mailer(resendOnly).channel()).isEqualTo(ContactMailer.Channel.RESEND);
    }

    @Test
    void logsOnlyWhenNothingConfigured() {
        MailProperties none = new MailProperties("owner@example.com", "", "", "", "");
        ContactMailer mailer = mailer(none);
        assertThat(mailer.channel()).isEqualTo(ContactMailer.Channel.NONE);
        assertThat(mailer.send(msg)).isTrue();
    }
}
