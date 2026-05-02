#!/usr/bin/env bash
# Post-create cleanup for the grails-react profile.
#
# Why this script exists:
# Grails 7's profile system concatenates skeleton files from the default web
# profile + this profile's skeleton, producing a duplicated build.gradle that
# does not compile (also includes a `mavenCentral` typo from the base profile).
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
