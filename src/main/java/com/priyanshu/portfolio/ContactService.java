package com.priyanshu.portfolio;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Receives contact-form messages: logs them (visible in the hosting platform's
 * logs), keeps the most recent ones in memory, and emails them to the owner.
 */
@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);
    private static final int MAX_KEPT = 200;

    public record Message(String name, String email, String message, Instant receivedAt) {}

    private final List<Message> messages = Collections.synchronizedList(new ArrayList<>());
    private final ContactMailer mailer;

    public ContactService(ContactMailer mailer) {
        this.mailer = mailer;
    }

    /** @return true if the message was delivered to the owner's inbox (or email is disabled) */
    public boolean submit(ContactForm form) {
        Message msg = new Message(form.getName(), form.getEmail(), form.getMessage(), Instant.now());
        synchronized (messages) {
            if (messages.size() >= MAX_KEPT) {
                messages.remove(0);
            }
            messages.add(msg);
        }
        log.info("New contact message from {} <{}>: {}", msg.name(), msg.email(), msg.message());
        return mailer.send(msg);
    }

    public List<Message> recent() {
        synchronized (messages) {
            return List.copyOf(messages);
        }
    }
}
