#!/usr/bin/env bash
# Installs the grails-react profile JAR into the local Maven repository (~/.m2)
# so that `grails create-app myapp --profile io.github.valentine101:grails-react:<version>` works.
#
# Usage: ./install.sh
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_FILE="$(ls "$SCRIPT_DIR"/grails-react-*.jar 2>/dev/null | head -1)"

if [[ -z "$JAR_FILE" ]]; then
    echo "ERROR: No grails-react-*.jar found in $SCRIPT_DIR" >&2
    exit 1
fi

VERSION="$(basename "$JAR_FILE" | sed -E 's/^grails-react-(.+)\.jar$/\1/')"

echo "Installing grails-react $VERSION to ~/.m2/repository/io/github/valentine101/grails-react/$VERSION/"

mvn install:install-file \
    -Dfile="$JAR_FILE" \
    -DgroupId=io.github.valentine101 \
    -DartifactId=grails-react \
    -Dversion="$VERSION" \
    -Dpackaging=jar

echo ""
echo "Installed. To create a new app:"
echo "  grails create-app my-app --profile io.github.valentine101:grails-react:$VERSION"
