package com.priyanshu.portfolio;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Emails contact-form messages to the site owner, with Reply-To set to the visitor.
 *
 * <p>Picks the first configured channel:
 * <ol>
 *   <li><b>Gmail SMTP</b> (MAIL_USERNAME + MAIL_PASSWORD). Note that Render's free plan blocks SMTP.</li>
 *   <li><b>Resend</b> HTTPS API (RESEND_API_KEY), which works on Render's free plan.</li>
 *   <li>Neither: messages are only logged by {@link ContactService}.</li>
 * </ol>
 */
@Component
public class ContactMailer {

    private static final Logger log = LoggerFactory.getLogger(ContactMailer.class);
    private static final String RESEND_URL = "https://api.resend.com/emails";

    enum Channel { GMAIL, RESEND, NONE }

    private final MailProperties props;
    private final RestClient http;
    private final JavaMailSender smtp;
    private final Channel channel;

    public ContactMailer(MailProperties props, RestClient.Builder builder,
                         ObjectProvider<JavaMailSender> smtp) {
        this.props = props;
        this.http = builder.build();
        this.smtp = smtp.getIfAvailable();
        if (props.gmailEnabled() && this.smtp != null) {
            channel = Channel.GMAIL;
        } else if (props.resendEnabled()) {
            channel = Channel.RESEND;
        } else {
            channel = Channel.NONE;
        }
        switch (channel) {
            case GMAIL -> log.info("Contact emails: Gmail SMTP as {} -> {}", props.gmailUsername(), props.to());
            case RESEND -> log.info("Contact emails: Resend -> {}", props.to());
            case NONE -> log.warn("No MAIL_USERNAME/MAIL_PASSWORD or RESEND_API_KEY set: "
                    + "contact messages will be logged but not emailed.");
        }
    }

    Channel channel() {
        return channel;
    }

    /** @return true if the email was accepted (or email is disabled), false if sending failed */
    public boolean send(ContactService.Message msg) {
        return switch (channel) {
            case GMAIL -> sendViaGmail(msg);
            case RESEND -> sendViaResend(msg);
            case NONE -> true;
        };
    }

    private boolean sendViaGmail(ContactService.Message msg) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(props.gmailFrom());
        mail.setTo(props.to());
        mail.setReplyTo(msg.email());
        mail.setSubject(subject(msg));
        mail.setText(body(msg));
        try {
            smtp.send(mail);
            log.info("Emailed contact message from {} via Gmail to {}", msg.email(), props.to());
            return true;
        } catch (MailException e) {
            log.error("Gmail SMTP failed for message from {}: {}", msg.email(), e.getMessage());
            return false;
        }
    }

    private boolean sendViaResend(ContactService.Message msg) {
        Map<String, Object> payload = Map.of(
                "from", props.resendFrom(),
                "to", List.of(props.to()),
                "reply_to", msg.email(),
                "subject", subject(msg),
                "text", body(msg));
        try {
            http.post()
                    .uri(RESEND_URL)
                    .header("Authorization", "Bearer " + props.resendApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Emailed contact message from {} via Resend to {}", msg.email(), props.to());
            return true;
        } catch (RestClientException e) {
            log.error("Resend failed for message from {}: {}", msg.email(), e.getMessage());
            return false;
        }
    }

    private static String subject(ContactService.Message msg) {
        // Strip line breaks so a crafted name can't inject extra mail headers.
        return "Portfolio: new message from " + msg.name().replaceAll("[\\r\\n]+", " ");
    }

    private static String body(ContactService.Message msg) {
        return """
                You have a new message from your portfolio contact form.

                Name:  %s
                Email: %s

                %s

                Reply to this email to answer %s directly.
                """.formatted(msg.name(), msg.email(), msg.message(), msg.name());
    }
}
