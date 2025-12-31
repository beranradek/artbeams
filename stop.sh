#!/bin/bash

echo "=== Stopping ArtBeams Application ==="

if [ -f .app.pid ]; then
    APP_PID=$(cat .app.pid)
    echo "Stopping application with PID: $APP_PID"

    # Try graceful shutdown first
    kill $APP_PID 2>/dev/null || true

    # Wait for process to stop
    sleep 3

    # Force kill if still running
    if ps -p $APP_PID > /dev/null 2>&1; then
        echo "Process still running, forcing shutdown..."
        kill -9 $APP_PID 2>/dev/null || true
    fi

    rm .app.pid
    echo "✓ Application stopped"
else
    echo "No .app.pid file found. Trying to find and stop Gradle/Java processes..."
    pkill -f "bootRun" || echo "No bootRun processes found"
    echo "✓ Cleanup complete"
fi

# Also stop any Gradle daemon
./gradlew --stop 2>/dev/null || true
