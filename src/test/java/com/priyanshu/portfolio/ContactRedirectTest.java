package com.priyanshu.portfolio;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Runs against a real embedded Tomcat, which MockMvc doesn't do. Tomcat is what
 * rewrites redirect URLs with ";jsessionid=" for first-time visitors.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContactRedirectTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void contactRedirectKeepsSessionIdOutOfUrl() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("name", "First Visitor");
        form.add("email", "visitor@example.com");
        form.add("message", "Hello from a brand new visitor!");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<String> response = rest.postForEntity("/contact", new HttpEntity<>(form, headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString())
                .doesNotContain("jsessionid")
                .endsWith("/#contact");
    }

    @Test
    void unknownPathIsNotA500() {
        ResponseEntity<String> response = rest.getForEntity("/;jsessionid=ABC", String.class);
        assertThat(response.getStatusCode().is5xxServerError()).isFalse();
    }
}
