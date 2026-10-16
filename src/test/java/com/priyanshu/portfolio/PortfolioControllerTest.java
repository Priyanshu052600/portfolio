package com.priyanshu.portfolio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PortfolioControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ContactService contactService;

    @Test
    void homeRendersResumeContent() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(content().string(containsString("Priyanshu Gupta")))
                .andExpect(content().string(containsString("Listings AI Optimizer")))
                .andExpect(content().string(containsString("Tata Consultancy Services")))
                .andExpect(content().string(containsString("priyanshugupta78600@gmail.com")));
    }

    @Test
    void projectsShowOnlyTrueLinqWithItsSite() throws Exception {
        mvc.perform(get("/"))
                .andExpect(content().string(containsString("href=\"https://truelinq.com\"")))
                .andExpect(content().string(containsString("Visit truelinq.com")))
                .andExpect(content().string(not(containsString("Hotel Review Management System"))))
                .andExpect(content().string(not(containsString("Hello, Magnificent"))));
    }

    @Test
    void homeDoesNotExposePhoneNumber() throws Exception {
        mvc.perform(get("/"))
                .andExpect(content().string(not(containsString("7985731782"))));
    }

    @Test
    void resumeIsDownloadable() throws Exception {
        mvc.perform(get("/resume/Priyanshu_Gupta_Resume.pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
    }

    @Test
    void validContactMessageRedirectsWithThanks() throws Exception {
        int before = contactService.recent().size();
        mvc.perform(post("/contact")
                        .param("name", "Recruiter")
                        .param("email", "recruiter@example.com")
                        .param("message", "We'd love to talk about a senior role."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/#contact"))
                .andExpect(flash().attribute("contactSent", "Recruiter"));
        assertThat(contactService.recent()).hasSize(before + 1);
    }

    @Test
    void invalidContactMessageShowsErrors() throws Exception {
        mvc.perform(post("/contact")
                        .param("name", "")
                        .param("email", "not-an-email")
                        .param("message", "hi"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeHasFieldErrors("contactForm", "name", "email", "message"))
                .andExpect(content().string(containsString("Please enter a valid email address.")));
    }

    @Test
    void honeypotSubmissionIsDropped() throws Exception {
        int before = contactService.recent().size();
        mvc.perform(post("/contact")
                        .param("name", "Bot")
                        .param("email", "bot@example.com")
                        .param("message", "Buy cheap stuff now please")
                        .param("website", "http://spam.example"))
                .andExpect(status().is3xxRedirection());
        assertThat(contactService.recent()).hasSize(before);
    }
}
