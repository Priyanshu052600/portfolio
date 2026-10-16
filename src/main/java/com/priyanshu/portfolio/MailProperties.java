package com.priyanshu.portfolio;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Where contact-form emails go and how they're sent. Values come from
 * environment variables (see application.properties), so secrets never
 * live in the code.
 */
@ConfigurationProperties(prefix = "contact.mail")
public record MailProperties(
        String to,
        String gmailUsername,
        String gmailPassword,
        String resendApiKey,
        String resendFrom) {

    public boolean gmailEnabled() {
        return hasText(gmailUsername) && hasText(gmailPassword);
    }

    public boolean resendEnabled() {
        return hasText(resendApiKey);
    }

    /** Gmail only sends as the signed-in account, so the From address is that account. */
    public String gmailFrom() {
        return "Portfolio Contact <" + gmailUsername + ">";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
