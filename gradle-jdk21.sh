#!/bin/bash
# Use Java 21 for Gradle commands.
#
# This script prefers:
# 1) already-set JAVA_HOME (if valid)
# 2) JDK21_HOME (if set + valid)
# 3) JAVA_HOME derived from `java` on PATH
# 4) common Linux install locations

set -euo pipefail

is_valid_java_home() {
  local home="$1"
  [[ -n "${home}" && -x "${home}/bin/java" ]]
}

if is_valid_java_home "${JAVA_HOME:-}"; then
  exec ./gradlew "$@"
fi

if is_valid_java_home "${JDK21_HOME:-}"; then
  export JAVA_HOME="${JDK21_HOME}"
  exec ./gradlew "$@"
fi

if command -v java >/dev/null 2>&1; then
  java_bin="$(command -v java)"
  # Resolve symlinks if possible
  if command -v readlink >/dev/null 2>&1; then
    java_bin="$(readlink -f "$java_bin" || true)"
  fi
  guessed_home="$(cd "$(dirname "$java_bin")/.." && pwd -P)"
  if is_valid_java_home "${guessed_home}"; then
    export JAVA_HOME="${guessed_home}"
    exec ./gradlew "$@"
  fi
fi

for candidate in \
  /usr/lib/jvm/java-21-openjdk-amd64 \
  /usr/lib/jvm/java-21-openjdk \
  /usr/lib/jvm/temurin-21-jdk-amd64 \
  /usr/lib/jvm/temurin-21-jdk \
  /opt/java/openjdk \
  /opt/jdk-21 \
; do
  if is_valid_java_home "$candidate"; then
    export JAVA_HOME="$candidate"
    exec ./gradlew "$@"
  fi
done

echo "ERROR: Java 21 not found. Set JAVA_HOME (or JDK21_HOME) to a valid JDK 21 installation." >&2
echo "Tip: 'which java' and 'readlink -f \$(which java)' can help you find the correct path." >&2
exit 1
