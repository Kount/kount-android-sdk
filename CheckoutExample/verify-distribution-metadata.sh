#!/bin/bash

# Script to verify distribution metadata in SDK jar

# Get the script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( dirname "$SCRIPT_DIR" )"

JAR_FILE="${PROJECT_ROOT}/KountDataCollector/kount-data-collector-5.0.3.jar"
TEMP_DIR="/tmp/jar-inspection-$$"

if [ ! -f "$JAR_FILE" ]; then
    echo "❌ JAR file not found: $JAR_FILE"
    exit 1
fi

echo "📦 Inspecting SDK JAR for distribution metadata..."
echo ""

# Extract and check for distribution properties
mkdir -p "$TEMP_DIR"
cd "$TEMP_DIR"
unzip -q "$JAR_FILE"

if [ -f "META-INF/distribution.properties" ]; then
    echo "✓ Found: META-INF/distribution.properties"
    echo ""
    echo "Content:"
    cat META-INF/distribution.properties
else
    echo "⚠ Not found: META-INF/distribution.properties"
    echo ""
    echo "META-INF contents:"
    ls -la META-INF/ 2>/dev/null || echo "META-INF directory not found"
fi

echo ""
echo "Cleaning up..."
cd - > /dev/null
rm -rf "$TEMP_DIR"
