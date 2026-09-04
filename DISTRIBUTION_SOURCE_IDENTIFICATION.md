# Distribution Source Identification Feature

## Overview

The Distribution Source Identification feature allows the Kount Data Collector to identify whether it was distributed through **JitPack** or directly from **GitHub releases**. This is useful for tracking usage patterns, debugging distribution-specific issues, and providing better support.

## Version Information

- **Feature Version**: 5.0.3
- **Feature Branch**: `feature/distribution-source-identification`
- **Created**: 2026-09-03

## ✅ Cómo funciona automáticamente

### 0. **JAR Base (en el Repositorio)**

El JAR incluido en el repositorio (`KountDataCollector/kount-data-collector-5.0.3.jar`) YA tiene:
```
META-INF/distribution.properties:
  distribution.source=github    # ← Default para GitHub
```

### 1. **Detección de Fuente (Gradle)**

En `CheckoutExample/app/build.gradle`:

```gradle
def detectDistributionSource = {
    def isJitPack = System.getenv('JITPACK') == 'true' || 
                    project.hasProperty('jitpack') ||
                    System.getenv('BUILD_NUMBER') != null
    return isJitPack ? 'jitpack' : 'github'
}
```

**Cuándo se ejecuta:**
- ✅ Si `JITPACK=true` → `distributionSource = 'jitpack'`
- ✅ Si no → `distributionSource = 'github'` (default)

---

### 2. **Inyección en el JAR (Tarea Gradle)**

Antes de cada build, la tarea `injectDistributionMetadata`:

```gradle
task injectDistributionMetadata {
    1. Extrae el JAR completo
    2. Crea META-INF/distribution.properties con:
       - distribution.source=jitpack|github
       - distribution.version=5.0.3
       - build.timestamp=<timestamp>
    3. Reempaqueta el JAR
    4. Crea backup (original.jar)
}
```

**Se ejecuta automáticamente:** `preBuild.dependsOn injectDistributionMetadata`

---

### 3. **Flujo de Uso del Cliente**

#### **Escenario A: Cliente usa GitHub (Default)**

```
Cliente obtiene JAR del repositorio
  ├─ kount-data-collector-5.0.3.jar
  └─ YA INCLUYE: META-INF/distribution.properties
     distribution.source=github

  ↓

Cliente ejecuta: gradle build (en su máquina local)
  
  ↓
  
Gradle detecta: JITPACK ≠ true → 'github'
  
  ↓
  
injectDistributionMetadata:
  ├─ Extrae JAR (que ya tiene 'github')
  ├─ Actualiza: distribution.source=github
  └─ Reempaqueta con metadata actualizada
  
  ↓

App Cliente con SDK
  └─ Envía: X-Distribution-Source: github
```

#### **Escenario B: JitPack Build**

```
JitPack clona el repositorio
  ├─ Obtiene: kount-data-collector-5.0.3.jar
  └─ YA INCLUYE: META-INF/distribution.properties
     distribution.source=github

  ↓

JitPack ejecuta: gradle build (con JITPACK=true)
  
  ↓
  
Gradle detecta: JITPACK=true → 'jitpack'
  
  ↓
  
injectDistributionMetadata:
  ├─ Extrae JAR (que tiene 'github')
  ├─ SOBRESCRIBE: distribution.source=jitpack  ← ¡Aquí está la magia!
  └─ Reempaqueta con metadata jitpack
  
  ↓

JitPack compila AAR/APK
  └─ Incluye SDK con: X-Distribution-Source: jitpack

  ↓

Cliente descarga desde JitPack
  └─ Recibe AAR ya compilado con metadata jitpack
```

---

## 🔍 Verificación

Usa el script para inspeccionar el JAR después del build:

```bash
./CheckoutExample/verify-distribution-metadata.sh
```

**Output esperado cuando se ejecute después de un build:**

```
📦 Inspecting SDK JAR for distribution metadata...

✓ Found: META-INF/distribution.properties

Content:
# Kount Data Collector - Distribution Source Identification
# Auto-generated during build

distribution.source=jitpack
distribution.version=5.0.3
build.timestamp=1725376234567
build.gradle.version=7.4.2
build.os=Mac OS X
```

---

## 🚀 Pasos para Verificar en JitPack

1. **Push a GitHub** (rama `feature/distribution-source-identification`)
2. **En JitPack**, dispara un build manual
3. **Descarga el AAR/JAR** resultante
4. **Extrae y verifica:**
   ```bash
   unzip app-release.aar
   cat META-INF/distribution.properties
   ```
5. **Verifica que tenga:** `distribution.source=jitpack`

---

## 🎯 Respuesta a tu pregunta

**"¿Si un cliente descarga el SDK desde jitpack entonces el SDK enviará el valor 'jitpack' en el 'distribution'?"**

✅ **SÍ, automáticamente:**

El flujo es:
1. **JAR Base (en repo)**: Incluye `distribution.source=github` por defecto
2. **Cliente descarga JAR**: Ya tiene metadata github
3. **Si JitPack compila**: 
   - Detecta `JITPACK=true`
   - Extrae el JAR (que tiene 'github')
   - **SOBRESCRIBE** con `distribution.source=jitpack`
   - Reempaqueta y distribuye AAR con jitpack
4. **Si es GitHub release**: 
   - Mantiene `distribution.source=github`
   - Distribuye JAR/AAR con github

**Sin que el cliente haga nada adicional** ✨

---

**Analogía visual:**
```
┌─────────────────────────┐
│ JAR base en repositorio │
│ distribution.source=    │
│      github ← DEFAULT   │
└────────────┬────────────┘
             │
    ┌────────┴────────┐
    │                 │
    ▼                 ▼
  GitHub           JitPack
  ┌──────┐         ┌──────┐
  │github│         │github│ (primero)
  └──────┘         └───┬──┘
                       │
                 Sobrescribe
                       │
                   ┌───▼──┐
                   │jitpack│ (final)
                   └───────┘
```

---

## 📝 Archivos clave

- `CheckoutExample/app/build.gradle` - Tarea de inyección + detección
- `CheckoutExample/app/src/main/res/raw/distribution_config.properties` - Config template
- `CheckoutExample/verify-distribution-metadata.sh` - Script de verificación
- `build-distribution.sh` - Script auxiliar
- `distribution.gradle` - Configuración de gradle reutilizable
- `distribution-github.properties` - Config para GitHub
- `distribution-jitpack.properties` - Config para JitPack

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
