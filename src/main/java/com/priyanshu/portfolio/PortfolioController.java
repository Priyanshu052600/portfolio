package com.priyanshu.portfolio;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PortfolioController {

    private final Portfolio portfolio;
    private final ContactService contactService;

    public PortfolioController(Portfolio portfolio, ContactService contactService) {
        this.portfolio = portfolio;
        this.contactService = contactService;
    }

    @ModelAttribute("p")
    public Portfolio portfolio() {
        return portfolio;
    }

    @GetMapping("/")
    public String home(Model model) {
        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", new ContactForm());
        }
        return "home";
    }

    @PostMapping("/contact")
    public String contact(@Valid @ModelAttribute("contactForm") ContactForm form,
                          BindingResult result,
                          RedirectAttributes redirect) {
        if (result.hasErrors()) {
            // Re-render in place so the visitor keeps what they typed and sees the errors.
            return "home";
        }
        // Silently drop bot submissions that filled the hidden honeypot field.
        boolean isBot = form.getWebsite() != null && !form.getWebsite().isBlank();
        boolean delivered = isBot || contactService.submit(form);
        if (delivered) {
            redirect.addFlashAttribute("contactSent", form.getName());
        } else {
            redirect.addFlashAttribute("contactFailed", true);
        }
        // Post/Redirect/Get so a page refresh doesn't resubmit the form.
        return "redirect:/#contact";
    }
}
