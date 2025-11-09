#!/bin/bash
# Use Java 21 for Gradle commands
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
exec ./gradlew "$@"
