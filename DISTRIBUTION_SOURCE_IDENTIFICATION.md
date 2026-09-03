# Distribution Source Identification Feature

## Overview

The Distribution Source Identification feature allows the Kount Data Collector to identify whether it was distributed through **JitPack** or directly from **GitHub releases**. This is useful for tracking usage patterns, debugging distribution-specific issues, and providing better support.

## Version Information

- **Feature Version**: 5.0.3
- **Feature Branch**: `feature/distribution-source-identification`
- **Created**: 2026-09-03

## Architecture

### Components

1. **distribution.gradle** - Gradle configuration for managing distribution metadata
2. **distribution-github.properties** - Metadata for GitHub distribution
3. **distribution-jitpack.properties** - Metadata for JitPack distribution
4. **DistributionSourceIdentifier.java** - Java utility class for Android apps
5. **DistributionSourceIdentifier.kt** - Kotlin version of the utility class
6. **build-distribution.sh** - Shell script to create distribution-specific jars

### How It Works

#### Jar Naming Convention

The feature supports three jar naming patterns:

```
kount-data-collector-5.0.3.jar              # Default (generic)
kount-data-collector-5.0.3-github.jar       # GitHub distribution
kount-data-collector-5.0.3-jitpack.jar      # JitPack distribution
```

#### Metadata Structure

Each distribution jar contains a `distribution.properties` file at the root that includes:

```properties
distribution.source=github|jitpack
version.name=5.0.3
distribution.repository=<url>
distribution.release.type=<type>
build.timestamp=<timestamp>
build.gradle.version=<gradle-version>
```

## Usage

### For App Developers

#### Java Example

```java
import com.kount.checkoutexample.utils.DistributionSourceIdentifier;

// Identify the distribution source
DistributionSourceIdentifier.DistributionSource source = 
    DistributionSourceIdentifier.identifySource();

// Get human-readable description
String description = DistributionSourceIdentifier.getSourceDescription(source);

// Log distribution info
Log.d("Kount", "Using " + description + " distribution");

// Get full metadata
String metadata = DistributionSourceIdentifier.getDistributionMetadata();
```

#### Kotlin Example

```kotlin
import com.kount.checkoutexample.kotlin.utils.DistributionSourceIdentifier

// Identify the distribution source
val source = DistributionSourceIdentifier.identifySource()

// Get human-readable description
val description = DistributionSourceIdentifier.getSourceDescription(source)

// Log distribution info
Log.d("Kount", "Using $description distribution")

// Get full metadata
val metadata = DistributionSourceIdentifier.getDistributionMetadata()
```

#### Identify from Filename

```java
// If you have the jar filename
String jarName = "kount-data-collector-5.0.3-jitpack.jar";
DistributionSourceIdentifier.DistributionSource source = 
    DistributionSourceIdentifier.identifySourceFromFilename(jarName);
```

### Creating Distribution-Specific Jars

Use the `build-distribution.sh` script to generate distribution-specific jars:

```bash
cd /path/to/sdk-github-source
./build-distribution.sh
```

This will create:
- `KountDataCollector/kount-data-collector-5.0.3-github.jar`
- `KountDataCollector/kount-data-collector-5.0.3-jitpack.jar`

### Switching Distributions

To use a specific distribution:

```bash
# For GitHub distribution
cp KountDataCollector/kount-data-collector-5.0.3-github.jar \
   KountDataCollector/kount-data-collector-5.0.3.jar

# For JitPack distribution
cp KountDataCollector/kount-data-collector-5.0.3-jitpack.jar \
   KountDataCollector/kount-data-collector-5.0.3.jar
```

## Gradle Configuration

The `distribution.gradle` script provides:

```gradle
// Get distribution source
def distSource = detectDistributionSourceFromJar('kount-data-collector-5.0.3-github.jar')

// Generate metadata
def metadataFile = generateDistributionProperties('github', buildDir)

// Access distribution sources enum
def sources = DISTRIBUTION_SOURCES
```

## Distribution Source Enum

Both Java and Kotlin utilities include a `DistributionSource` enum with the following values:

- `GITHUB` - GitHub release distribution
- `JITPACK` - JitPack build distribution
- `UNKNOWN` - Unknown or unidentified source

## Metadata Properties

### Common Properties

| Property | Description | Example |
|----------|-------------|---------|
| `distribution.source` | Source identifier | `github` or `jitpack` |
| `version.name` | SDK version | `5.0.3` |
| `build.timestamp` | Build timestamp | `1693651200000` |
| `build.gradle.version` | Gradle version used | `8.0` |

### GitHub-Specific Properties

| Property | Description |
|----------|-------------|
| `distribution.repository` | GitHub repository URL |
| `distribution.release.type` | Always `github-release` |

### JitPack-Specific Properties

| Property | Description |
|----------|-------------|
| `distribution.repository` | JitPack URL |
| `distribution.release.type` | Always `jitpack-build` |

## Integration Points

### app/build.gradle

The app's build script can integrate distribution detection:

```gradle
afterEvaluate {
    // Read distribution metadata after building
    def distFile = new File('../../distribution-github.properties')
    if (distFile.exists()) {
        // Integration logic
    }
}
```

### Version Management

The feature respects the existing version system:
- Version is sourced from `version.properties`
- Distribution metadata is additive (doesn't replace version info)
- All distribution variants have the same version

## Testing

### Manual Testing

1. **Verify Jar Contents:**
```bash
jar tf KountDataCollector/kount-data-collector-5.0.3-github.jar | grep distribution.properties
```

2. **Extract and View Metadata:**
```bash
jar xf KountDataCollector/kount-data-collector-5.0.3-github.jar distribution.properties
cat distribution.properties
```

3. **Test in App:**
   - Build and run the example apps
   - Use the utility classes to identify source
   - Verify logging output shows correct distribution

### Automated Testing

Create a test that verifies:
- [ ] Correct jar is being used
- [ ] Metadata file exists and is readable
- [ ] Correct source is identified
- [ ] Fallback behavior works when metadata is missing

## Future Enhancements

1. **Automatic Detection** - Auto-detect distribution source during build
2. **Build Variants** - Create separate build variants for each distribution
3. **Metrics** - Send distribution source to analytics
4. **Documentation** - Add distribution info to README
5. **CI/CD Integration** - Automate jar creation for each distribution

## Troubleshooting

### Metadata Not Found

If the utility returns `DistributionSource.UNKNOWN`:

1. Check if jar contains `distribution.properties`
2. Verify the properties file is at the root of the jar
3. Check for typos in property names
4. Ensure jar was created with the build script

### Version Mismatch

If you see different versions in different distributions:

1. Verify `version.properties` is consistent
2. Rebuild both distribution jars
3. Check that you're using the correct jar copy

## Related Files

- [CHANGELOG.md](../CHANGELOG.md) - Release notes
- [version.properties](CheckoutExample/version.properties) - Version configuration
- [README.md](../README.md) - Project README
- [jitpack.yml](../jitpack.yml) - JitPack configuration
