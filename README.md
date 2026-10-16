# Priyanshu Gupta · Portfolio

A clean, recruiter-first developer portfolio built with Spring Boot MVC and Thymeleaf.
It includes light/dark mode, impact stats, a career timeline, projects, skills,
achievements, a resume download and a validated contact form.

## Update the content
All text lives in **`src/main/resources/portfolio.yml`**: summary, stats, jobs,
projects, skills, achievements and contact links. Edit it, restart, and you're done.
No Java changes needed.

To replace the resume, overwrite `src/main/resources/static/resume/Priyanshu_Gupta_Resume.pdf`.

## Run locally
```bash
mvn spring-boot:run
```
Then open http://localhost:8080

## Test
```bash
mvn test
```

## Contact form messages
Messages are validated, protected by a honeypot field against bots, written to the
application log (`New contact message from …`) and kept in memory. On Render, read
them under **Logs**. To get them by email, wire `ContactService` to an email API
such as Resend or SendGrid. Render's free plan blocks SMTP ports.

## Docker
```bash
docker build -t portfolio .
docker run -p 8080:8080 portfolio
```

## CI/CD
- **CI:** `.github/workflows/ci.yml` runs the tests, builds the jar and checks that
  the Docker image builds on every push to `main` and on every pull request.
- **CD:** `render.yaml` is a Render Blueprint (free plan). Every push to `main`
  redeploys automatically.

## Structure
```
src/main/java/com/priyanshu/portfolio/
├── PortfolioApplication.java   # entry point
├── Portfolio.java              # typed content bound from portfolio.yml
├── PortfolioController.java    # GET / and POST /contact
├── ContactForm.java            # validated contact form
└── ContactService.java         # logs and stores contact messages
src/main/resources/
├── portfolio.yml               # ← all site content
├── templates/index.html        # single-page layout
├── static/css/style.css        # design tokens, light/dark, responsive
├── static/js/main.js           # theme toggle, scroll reveal, count-up
└── static/resume/              # downloadable resume PDF
```
