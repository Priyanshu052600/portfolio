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

## Contact form → your Gmail
When a visitor clicks **Send message**, the message is validated, checked against a
honeypot field for bots, logged, and emailed to `priyanshugupta78600@gmail.com` through
[Resend](https://resend.com). Reply-To is set to the visitor, so you can just hit Reply.
Resend sends over HTTPS, because Render's free plan blocks SMTP.

**One-time setup**
1. Sign up at https://resend.com **using priyanshugupta78600@gmail.com**. Without a custom
   domain, Resend only delivers to the email you signed up with.
2. Go to **API Keys → Create API key** (permission: *Sending access*) and copy it.
3. Provide it as the `RESEND_API_KEY` environment variable. Never commit it.
   - Locally: `RESEND_API_KEY=re_xxx mvn spring-boot:run`
   - Render: service → **Environment** → `RESEND_API_KEY`

If `RESEND_API_KEY` isn't set, messages are only logged (on Render, under **Logs**).
If sending fails, the visitor sees an error asking them to email you directly.

| Variable | Default | Purpose |
|---|---|---|
| `RESEND_API_KEY` | *(none)* | Turns email delivery on |
| `CONTACT_TO_EMAIL` | `priyanshugupta78600@gmail.com` | Inbox that receives messages |
| `CONTACT_FROM_EMAIL` | `Portfolio Contact <onboarding@resend.dev>` | Sender (use your own domain once verified in Resend) |

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
