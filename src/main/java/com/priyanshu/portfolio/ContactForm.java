package com.priyanshu.portfolio;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Form-backing object for the "Get in touch" form. */
public class ContactForm {

    @NotBlank(message = "Please enter your name.")
    @Size(max = 80, message = "Name must be 80 characters or fewer.")
    private String name;

    @NotBlank(message = "Please enter your email.")
    @Email(message = "Please enter a valid email address.")
    @Size(max = 120)
    private String email;

    @NotBlank(message = "Please write a short message.")
    @Size(min = 10, max = 2000, message = "Message must be between 10 and 2000 characters.")
    private String message;

    /** Hidden honeypot field; real visitors leave it empty, bots tend to fill it. */
    private String website;

    public String getName() { return name; }
    public void setName(String name) { this.name = trim(name); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = trim(email); }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = trim(message); }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
