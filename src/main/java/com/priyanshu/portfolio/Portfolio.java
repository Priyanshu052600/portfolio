package com.priyanshu.portfolio;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** All site content, bound from portfolio.yml. */
@ConfigurationProperties(prefix = "portfolio")
public record Portfolio(
        String name,
        String initials,
        String role,
        String headline,
        String tagline,
        String location,
        String openTo,
        LocalDate careerStart,
        String resumeFile,
        List<String> about,
        Contact contact,
        List<Stat> stats,
        List<Job> experience,
        List<Project> projects,
        List<SkillGroup> skills,
        List<Achievement> achievements,
        Education education) {

    /** Whole years of professional experience, e.g. 5 for "5+ years". */
    public int yearsOfExperience() {
        return Period.between(careerStart, LocalDate.now()).getYears();
    }

    public record Contact(String email, String linkedin, String github) {}

    public record Stat(String value, String label) {}

    public record Job(String company, String role, String period, String location,
                      boolean current, List<String> highlights, List<String> stack) {}

    public record Project(String name, String year, String description,
                          List<String> tags, String link) {}

    public record SkillGroup(String group, List<String> items) {}

    public record Achievement(String icon, String title, String detail) {}

    public record Education(String school, String degree, String period, String score) {}
}
