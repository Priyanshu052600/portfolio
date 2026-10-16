// Theme toggle, scroll reveal, stat count-up and active nav link.
(function () {
    const root = document.documentElement;
    root.classList.add("js");

    // ---------- theme toggle ----------
    const isDark = () =>
        root.dataset.theme
            ? root.dataset.theme === "dark"
            : window.matchMedia("(prefers-color-scheme: dark)").matches;

    document.querySelector(".theme-toggle")?.addEventListener("click", () => {
        const next = isDark() ? "light" : "dark";
        root.dataset.theme = next;
        try { localStorage.setItem("theme", next); } catch (e) { /* storage unavailable */ }
    });

    // ---------- sticky nav border ----------
    const nav = document.querySelector(".nav");
    const onScroll = () => nav.classList.toggle("scrolled", scrollY > 8);
    addEventListener("scroll", onScroll, { passive: true });
    onScroll();

    // ---------- reveal on scroll ----------
    const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    const reveals = document.querySelectorAll(".reveal");
    if (reduceMotion || !("IntersectionObserver" in window)) {
        reveals.forEach(el => el.classList.add("visible"));
    } else {
        const io = new IntersectionObserver(entries => {
            entries.forEach(entry => {
                if (!entry.isIntersecting) return;
                const el = entry.target;
                // Stagger siblings that appear together.
                const index = [...el.parentElement.children].indexOf(el);
                el.style.transitionDelay = Math.min(index, 6) * 70 + "ms";
                el.classList.add("visible");
                io.unobserve(el);
            });
        }, { threshold: 0.12, rootMargin: "0px 0px -40px 0px" });
        reveals.forEach(el => io.observe(el));
    }

    // ---------- count-up stats ("40%", "300K+", "1M+", "5+") ----------
    const animateCount = el => {
        const raw = el.dataset.count || el.textContent;
        const match = raw.match(/^(\d+(?:\.\d+)?)(.*)$/);
        if (!match || reduceMotion) return;
        const target = parseFloat(match[1]);
        const suffix = match[2];
        const duration = 1400;
        const start = performance.now();
        const step = now => {
            const t = Math.min(1, (now - start) / duration);
            const eased = 1 - Math.pow(1 - t, 3);
            el.textContent = Math.round(target * eased) + suffix;
            if (t < 1) requestAnimationFrame(step);
        };
        requestAnimationFrame(step);
    };
    const statsBox = document.querySelector(".stats");
    if (statsBox && "IntersectionObserver" in window) {
        const so = new IntersectionObserver(entries => {
            if (entries.some(e => e.isIntersecting)) {
                statsBox.querySelectorAll(".stat-value").forEach(animateCount);
                so.disconnect();
            }
        }, { threshold: 0.4 });
        so.observe(statsBox);
    }

    // ---------- highlight the nav link for the section in view ----------
    const links = [...document.querySelectorAll(".nav-links a")];
    const sections = links.map(a => document.querySelector(a.getAttribute("href"))).filter(Boolean);
    if ("IntersectionObserver" in window) {
        const no = new IntersectionObserver(entries => {
            entries.forEach(entry => {
                if (!entry.isIntersecting) return;
                links.forEach(a => a.classList.toggle("active", a.getAttribute("href") === "#" + entry.target.id));
            });
        }, { rootMargin: "-45% 0px -50% 0px" });
        sections.forEach(s => no.observe(s));
    }

    // ---------- jump back to the form when the server returned errors ----------
    const firstError = document.querySelector(".contact-form .error");
    if (firstError) {
        document.querySelectorAll("#contact .reveal").forEach(el => el.classList.add("visible"));
        document.getElementById("contact").scrollIntoView();
        firstError.closest("label")?.querySelector("input, textarea")?.focus({ preventScroll: true });
    }

    // ---------- footer year ----------
    const year = document.getElementById("year");
    if (year) year.textContent = new Date().getFullYear();
})();
