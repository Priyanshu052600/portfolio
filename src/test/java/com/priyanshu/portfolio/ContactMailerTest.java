package com.priyanshu.portfolio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(ContactMailer.class)
@EnableConfigurationProperties(MailProperties.class)
@TestPropertySource(properties = {
        "contact.mail.resend-api-key=test-key",
        "contact.mail.to=owner@example.com",
        "contact.mail.from=Portfolio <onboarding@resend.dev>"
})
class ContactMailerTest {

    @Autowired
    private ContactMailer mailer;

    @Autowired
    private MockRestServiceServer server;

    private final ContactService.Message msg = new ContactService.Message(
            "Ada", "ada@example.com", "Let's talk about a senior role.", Instant.now());

    @Test
    void sendsEmailToOwnerWithReplyToVisitor() {
        server.expect(requestTo("https://api.resend.com/emails"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer test-key"))
                .andExpect(jsonPath("$.to[0]").value("owner@example.com"))
                .andExpect(jsonPath("$.reply_to").value("ada@example.com"))
                .andExpect(jsonPath("$.subject").value("Portfolio: new message from Ada"))
                .andRespond(withSuccess("{\"id\":\"email_123\"}", MediaType.APPLICATION_JSON));

        assertThat(mailer.send(msg)).isTrue();
        server.verify();
    }

    @Test
    void reportsFailureWhenApiErrors() {
        server.expect(requestTo("https://api.resend.com/emails")).andRespond(withServerError());

        assertThat(mailer.send(msg)).isFalse();
    }
}
