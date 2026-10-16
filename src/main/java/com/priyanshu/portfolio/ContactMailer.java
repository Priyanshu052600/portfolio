package com.priyanshu.portfolio;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Emails contact-form messages to the site owner through Resend's HTTPS API.
 * Uses HTTPS instead of SMTP because Render's free plan blocks SMTP ports.
 */
@Component
public class ContactMailer {

    private static final Logger log = LoggerFactory.getLogger(ContactMailer.class);
    private static final String RESEND_URL = "https://api.resend.com/emails";

    private final MailProperties props;
    private final RestClient http;

    public ContactMailer(MailProperties props, RestClient.Builder builder) {
        this.props = props;
        this.http = builder.build();
        if (!props.enabled()) {
            log.warn("RESEND_API_KEY is not set: contact messages will be logged but not emailed.");
        }
    }

    /**
     * Sends the message to the owner's inbox, with Reply-To set to the visitor.
     *
     * @return true if the email was accepted (or email is disabled), false if sending failed
     */
    public boolean send(ContactService.Message msg) {
        if (!props.enabled()) {
            return true;
        }
        Map<String, Object> body = Map.of(
                "from", props.from(),
                "to", List.of(props.to()),
                "reply_to", msg.email(),
                "subject", "Portfolio: new message from " + msg.name(),
                "text", """
                        You have a new message from your portfolio contact form.

                        Name:  %s
                        Email: %s

                        %s

                        Reply to this email to answer %s directly.
                        """.formatted(msg.name(), msg.email(), msg.message(), msg.name()));
        try {
            http.post()
                    .uri(RESEND_URL)
                    .header("Authorization", "Bearer " + props.resendApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Emailed contact message from {} to {}", msg.email(), props.to());
            return true;
        } catch (RestClientException e) {
            log.error("Failed to email contact message from {}: {}", msg.email(), e.getMessage());
            return false;
        }
    }
}
