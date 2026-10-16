package com.priyanshu.portfolio;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ContactDeliveryFailureTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ContactMailer mailer;

    @Test
    void showsFailureMessageWhenEmailCannotBeSent() throws Exception {
        when(mailer.send(any())).thenReturn(false);

        mvc.perform(post("/contact")
                        .param("name", "Recruiter")
                        .param("email", "recruiter@example.com")
                        .param("message", "We'd love to talk about a senior role."))
                .andExpect(redirectedUrl("/#contact"))
                .andExpect(flash().attribute("contactFailed", true))
                .andExpect(flash().attributeCount(1));
    }
}
