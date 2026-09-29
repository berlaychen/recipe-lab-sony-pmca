#!/usr/bin/env bash
# Offline localization checks. Full Android resource and APK validation is done by build.sh in CI.
set -euo pipefail
cd "$(dirname "$0")/.."

PYTHON="${PYTHON:-python3}"
if [ -n "${JAVA_HOME:-}" ]; then
  JAVAC="$JAVA_HOME/bin/javac"
  JAVA="$JAVA_HOME/bin/java"
else
  JAVAC=javac
  JAVA=java
fi

command -v "$PYTHON" >/dev/null || { echo "test-i18n: Python 3 is required" >&2; exit 1; }
command -v "$JAVAC" >/dev/null || { echo "test-i18n: a JDK is required" >&2; exit 1; }

"$PYTHON" tools/check-i18n.py

OUT=out/i18n-checks
rm -rf "$OUT"
mkdir -p "$OUT/classes"

"$JAVAC" -encoding UTF-8 --release 8 -Xlint:-options \
  -d "$OUT/classes" \
  src/com/voxivoid/recipelab/TextFlow.java \
  tools/i18n-tests/TextFlowChecks.java
"$JAVA" -cp "$OUT/classes" com.voxivoid.recipelab.TextFlowChecks
