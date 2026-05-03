#!/usr/bin/env bash
# Post-create cleanup for the grails-react profile.
#
# Why this script exists:
# This profile extends the Grails base profile, but the generated template is
# web based and keeps the Gradle structure Grails web applications use. Grails
# 7's profile system can still produce a starter build.gradle that is not the
# React-aware web build this template needs.
# This script replaces the broken merged build.gradle with our intended one
# and tidies up.
#
# Usage:
#   cd <new-app>
#   ./post-create.sh
set -euo pipefail

cd "$(dirname "$0")"

if [[ ! -f build.gradle.react ]]; then
    echo "ERROR: build.gradle.react not found. Already cleaned up?" >&2
    exit 1
fi

echo "Replacing merged build.gradle with the React-aware version..."
mv build.gradle.react build.gradle

if [[ -f gitignore.react ]]; then
    echo "Adding React build artifacts to .gitignore..."
    while IFS= read -r pattern; do
        [[ -z "$pattern" ]] && continue
        grep -qxF "$pattern" .gitignore || printf '%s\n' "$pattern" >> .gitignore
    done < gitignore.react
    rm -- gitignore.react
fi

echo "Removing post-create.sh..."
rm -- "$0"

echo ""
echo "Done. Next steps:"
echo "  ./gradlew build       # builds the Grails app + Vite frontend"
echo "  ./gradlew bootRun     # starts the app on http://localhost:8080"
echo ""
echo "For dev with HMR:"
echo "  ./gradlew bootRun -x buildFrontend &  # one terminal"
echo "  cd frontend && npm run dev            # another terminal (Vite on :5173)"
