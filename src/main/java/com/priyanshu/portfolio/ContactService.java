package com.priyanshu.portfolio;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Receives contact-form messages. Messages are logged (visible in the hosting
 * platform's logs) and kept in memory. Swap this for email or a database later.
 */
@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);
    private static final int MAX_KEPT = 200;

    public record Message(String name, String email, String message, Instant receivedAt) {}

    private final List<Message> messages = Collections.synchronizedList(new ArrayList<>());

    public void submit(ContactForm form) {
        Message msg = new Message(form.getName(), form.getEmail(), form.getMessage(), Instant.now());
        synchronized (messages) {
            if (messages.size() >= MAX_KEPT) {
                messages.remove(0);
            }
            messages.add(msg);
        }
        log.info("New contact message from {} <{}>: {}", msg.name(), msg.email(), msg.message());
    }

    public List<Message> recent() {
        synchronized (messages) {
            return List.copyOf(messages);
        }
    }
}
