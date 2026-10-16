package com.priyanshu.portfolio;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Where contact-form emails go. Values come from environment variables
 * (see application.properties) so the API key never lives in the code.
 */
@ConfigurationProperties(prefix = "contact.mail")
public record MailProperties(String resendApiKey, String to, String from) {

    public boolean enabled() {
        return resendApiKey != null && !resendApiKey.isBlank();
    }
}
