#!/bin/bash
set -e

echo "=== ArtBeams Development Environment Setup ==="

# Check for Java 21
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed. Please install Java 21."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "ERROR: Java 21 or higher required. Found: Java $JAVA_VERSION"
    exit 1
fi

echo "✓ Java $JAVA_VERSION detected"

# Check for application-local.yml configuration
if [ ! -f "src/main/resources/application-local.yml" ]; then
    echo "WARNING: application-local.yml not found."
    echo "Please copy application-local-template.yml to application-local.yml and configure it."
    if [ -f "src/main/resources/application-local-template.yml" ]; then
        echo "Template file exists at: src/main/resources/application-local-template.yml"
    fi
fi

# Clean and build the application
echo ""
echo "=== Building Application ==="
./gradlew clean build -x test

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "ERROR: Build failed. Please fix compilation errors first."
    exit 1
fi

echo ""
echo "✓ Build successful"

# Start the application in background
echo ""
echo "=== Starting Application ==="
echo "Starting Spring Boot application with local profile..."
./gradlew bootRun --args='--spring.profiles.active=local' &
APP_PID=$!
echo $APP_PID > .app.pid

echo ""
echo "✓ Application starting with PID: $APP_PID"
echo ""
echo "================================"
echo "Application will be available at: http://localhost:8080/"
echo "To stop the application, run: ./stop.sh"
echo "To view logs, run: tail -f build/logs/application.log (if configured)"
echo "================================"
