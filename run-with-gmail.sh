#!/usr/bin/env bash
# Runs the portfolio locally with contact-form emails sent through Gmail.
# Asks for the Gmail app password with hidden input, so it never shows on
# screen, in shell history or in any file.
set -euo pipefail
cd "$(dirname "$0")"

export MAIL_USERNAME="${MAIL_USERNAME:-pg012600@gmail.com}"
export PORT="${PORT:-8081}"

if [[ -z "${MAIL_PASSWORD:-}" ]]; then
    read -r -s -p "Gmail app password for $MAIL_USERNAME (input hidden): " MAIL_PASSWORD
    echo
    MAIL_PASSWORD="${MAIL_PASSWORD// /}"   # Google shows it with spaces; strip them
    export MAIL_PASSWORD
fi

echo "Starting portfolio on http://localhost:$PORT (sending as $MAIL_USERNAME)..."
exec mvn -q spring-boot:run
