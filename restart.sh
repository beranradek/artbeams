#!/bin/bash

echo "=== Restarting ArtBeams Application ==="

# Stop the application
./stop.sh

# Wait a moment
sleep 2

# Start the application
./start.sh
