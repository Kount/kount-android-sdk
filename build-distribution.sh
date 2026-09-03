#!/bin/bash

# Build script for creating Kount Data Collector jar with distribution source identification
# This script creates separate jars for jitpack and github distributions

set -e

# Configuration
VERSION="5.0.3"
CURRENT_JAR="KountDataCollector/kount-data-collector-${VERSION}.jar"
GITHUB_JAR="KountDataCollector/kount-data-collector-${VERSION}-github.jar"
JITPACK_JAR="KountDataCollector/kount-data-collector-${VERSION}-jitpack.jar"
TEMP_DIR=$(mktemp -d)

echo "=== Kount Data Collector Distribution Build Script ==="
echo "Version: ${VERSION}"
echo "Temp Directory: ${TEMP_DIR}"

# Function to create distribution-specific jar
create_distribution_jar() {
    local source=$1
    local input_jar=$2
    local output_jar=$3
    local props_file="distribution-${source}.properties"
    
    echo "Creating ${source} distribution jar..."
    
    # Copy the original jar
    cp "$input_jar" "$output_jar"
    
    # Create temporary directory structure
    local work_dir="${TEMP_DIR}/${source}"
    mkdir -p "$work_dir/META-INF"
    
    # Copy distribution properties into the jar
    if [ -f "$props_file" ]; then
        cp "$props_file" "$work_dir/distribution.properties"
        
        # Add to jar using zip
        cd "$work_dir"
        zip -q -r "$output_jar" distribution.properties
        cd - > /dev/null
    fi
    
    echo "  Created: $output_jar"
}

# Main build logic
if [ ! -f "$CURRENT_JAR" ]; then
    echo "Error: Source jar not found: $CURRENT_JAR"
    exit 1
fi

echo ""
echo "Creating distribution-specific jars..."

# Create GitHub distribution jar
create_distribution_jar "github" "$CURRENT_JAR" "$GITHUB_JAR"

# Create JitPack distribution jar
create_distribution_jar "jitpack" "$CURRENT_JAR" "$JITPACK_JAR"

echo ""
echo "Build Summary:"
echo "  Source JAR:  $CURRENT_JAR"
echo "  GitHub JAR:  $GITHUB_JAR"
echo "  JitPack JAR: $JITPACK_JAR"

# Verify jars were created
if [ -f "$GITHUB_JAR" ] && [ -f "$JITPACK_JAR" ]; then
    echo ""
    echo "✓ All distribution jars created successfully!"
else
    echo ""
    echo "✗ Error: Failed to create distribution jars"
    exit 1
fi

# Cleanup
rm -rf "$TEMP_DIR"

echo ""
echo "Note: To use a specific distribution, copy the corresponding jar to KountDataCollector/"
echo "Example for GitHub:  cp $GITHUB_JAR $CURRENT_JAR"
echo "Example for JitPack: cp $JITPACK_JAR $CURRENT_JAR"
