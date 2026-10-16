# Priyanshu Gupta · Portfolio

[![CI](https://github.com/Priyanshu052600/portfolio/actions/workflows/ci.yml/badge.svg)](https://github.com/Priyanshu052600/portfolio/actions/workflows/ci.yml)
[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/Priyanshu052600/portfolio)

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
honeypot field for bots, logged, and emailed to `priyanshugupta78600@gmail.com`.
Reply-To is set to the visitor, so you can just hit Reply. If sending fails, the
visitor sees an error asking them to email you directly.

The app picks the sender automatically, in this order:

| # | Channel | Enabled by | Works on |
|---|---|---|---|
| 1 | **Gmail SMTP** as `pg012600@gmail.com` | `MAIL_USERNAME` + `MAIL_PASSWORD` | Your laptop, paid hosts. **Not Render free** (SMTP blocked) |
| 2 | **Resend** HTTPS API | `RESEND_API_KEY` | Everywhere, including Render free |
| 3 | Log only | nothing set | Everywhere |

> ⚠️ Gmail wins when both are set. **Don't set `MAIL_USERNAME`/`MAIL_PASSWORD` on Render's
> free plan**, or every send will time out. Use `RESEND_API_KEY` there instead.

### Option 1: Gmail app password
1. On **pg012600@gmail.com**, turn on 2-Step Verification (myaccount.google.com → Security).
2. Create an app password at https://myaccount.google.com/apppasswords (name it "Portfolio").
3. Run with it. Never commit it or share it:
   ```bash
   MAIL_USERNAME=pg012600@gmail.com MAIL_PASSWORD=your_app_password mvn spring-boot:run
   ```
   The startup log says `Contact emails: Gmail SMTP as pg012600@gmail.com -> priyanshugupta78600@gmail.com`.

### Option 2: Resend (for Render free)
1. Sign up at https://resend.com **using priyanshugupta78600@gmail.com**. Without a custom
   domain, Resend only delivers to the email you signed up with.
2. **API Keys → Create API key** (*Sending access*).
3. Set `RESEND_API_KEY` (Render: service → **Environment**).

| Variable | Default | Purpose |
|---|---|---|
| `MAIL_USERNAME` | *(none)* | Gmail account that sends (e.g. `pg012600@gmail.com`) |
| `MAIL_PASSWORD` | *(none)* | That account's 16-character **app password** |
| `RESEND_API_KEY` | *(none)* | Resend API key |
| `CONTACT_TO_EMAIL` | `priyanshugupta78600@gmail.com` | Inbox that receives messages |
| `CONTACT_FROM_EMAIL` | `Portfolio Contact <onboarding@resend.dev>` | Resend sender (use your own domain once verified) |

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
