#!/bin/bash

# ArtBeams Development Environment Setup Script
# This script sets up and runs the development environment for ArtBeams CMS

set -e  # Exit on any error

echo "================================================"
echo "ArtBeams CMS - Development Environment Setup"
echo "================================================"
echo ""

# Check if Java 21 is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed. Please install Java 21."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "ERROR: Java 21 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✓ Java version check passed (Java $JAVA_VERSION)"

# Check if PostgreSQL is accessible
if ! command -v psql &> /dev/null; then
    echo "WARNING: psql not found in PATH. Make sure PostgreSQL is installed and running."
else
    echo "✓ PostgreSQL client found"
fi

# Check if application-local.yml exists
if [ ! -f "src/main/resources/application-local.yml" ]; then
    echo ""
    echo "WARNING: application-local.yml not found!"
    echo "Please create it from application-local-template.yml:"
    echo "  cp src/main/resources/application-local-template.yml src/main/resources/application-local.yml"
    echo ""
    echo "Then configure your database connection and API keys."
    echo ""
    read -p "Do you want to continue anyway? (y/n) " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
else
    echo "✓ Local configuration found"
fi

# Clean and build the project
echo ""
echo "Building project..."
echo "------------------------------------------------"
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "ERROR: Build failed. Please fix the errors and try again."
    exit 1
fi

echo ""
echo "✓ Build successful"

# Check if database schema needs to be initialized
echo ""
echo "NOTE: Make sure your PostgreSQL database is running and initialized."
echo "If this is your first run, initialize the database:"
echo ""
echo "  psql -U your_user -d your_database -f src/main/resources/sql/create_tables.sql"
echo "  psql -U your_user -d your_database -f src/main/resources/sql/insert_config.sql"
echo "  psql -U your_user -d your_database -f src/main/resources/sql/insert_localisation.sql"
echo "  psql -U your_user -d your_database -f src/main/resources/sql/insert_antispam_quiz.sql"
echo ""

# Generate JOOQ classes if needed
echo "Generating JOOQ classes..."
echo "------------------------------------------------"
./gradlew generateJooq

if [ $? -ne 0 ]; then
    echo "WARNING: JOOQ generation failed. This might be OK if database is not running yet."
    echo "You can generate JOOQ classes later with: ./gradlew generateJooq"
else
    echo "✓ JOOQ classes generated"
fi

echo ""
echo "================================================"
echo "Starting ArtBeams CMS..."
echo "================================================"
echo ""
echo "Application will be available at: http://localhost:8080/"
echo ""
echo "Default admin credentials:"
echo "  Username: admin"
echo "  Password: adminadmin"
echo "  (CHANGE THIS PASSWORD AFTER FIRST LOGIN!)"
echo ""
echo "Press Ctrl+C to stop the server"
echo "------------------------------------------------"
echo ""

# Run the application with local profile
./gradlew bootRun --args='--spring.profiles.active=local'
