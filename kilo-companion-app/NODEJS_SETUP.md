# Node.js Runtime Setup Guide

This document explains how to set up the Node.js runtime for CodePal Android app.

## Overview

CodePal embeds Node.js Mobile to run opencode and kilo CLI tools locally on Android. This requires:

1. **nodejs-mobile native libraries** (.so files)
2. **Node.js headers** for building the JNI bridge
3. **opencode/kilo npm packages** bundled as assets

## Quick Start

### Option 1: Automated Setup (Recommended)

Run the setup script from the `kilo-companion-app` directory:

```bash
cd kilo-companion-app
./setup-nodejs-mobile.sh
```

This will create the directory structure and provide download instructions.

### Option 2: Manual Setup

#### Step 1: Download nodejs-mobile

Download prebuilt binaries from the official releases:
- **URL**: https://github.com/nodejs-mobile/nodejs-mobile/releases
- **Current version**: 18.20.4
- **Files needed**: `nodejs-mobile-v18.20.4-android-{arch}.tar.gz`

For each architecture (arm64-v8a, armeabi-v7a, x86, x86_64):
```bash
# Example for arm64-v8a
curl -L -o nodejs-mobile-arm64.tar.gz \
  https://github.com/nodejs-mobile/nodejs-mobile/releases/download/.../nodejs-mobile-v18.20.4-android-arm64.tar.gz
```

#### Step 2: Extract Libraries

Extract to the appropriate directories:

```bash
# arm64-v8a (most modern phones)
tar -xzf nodejs-mobile-arm64.tar.gz -C app/src/main/jniLibs/arm64-v8a/

# armeabi-v7a (older ARM devices)
tar -xzf nodejs-mobile-armv7.tar.gz -C app/src/main/jniLibs/armeabi-v7a/

# x86_64 (emulators)
tar -xzf nodejs-mobile-x86_64.tar.gz -C app/src/main/jniLibs/x86_64/

# x86 (legacy emulators)
tar -xzf nodejs-mobile-x86.tar.gz -C app/src/main/jniLibs/x86/
```

Each directory should contain:
- `libnodejs-mobile.so`
- `libv8_libbase.so`
- `libv8_libplatform.so`

#### Step 3: Verify Headers

Headers are already included in `app/src/main/jniLibs/include/`. If you need to update them:

```bash
# Extract headers from nodejs-mobile
tar -xzf nodejs-mobile-headers.tar.gz -C app/src/main/jniLibs/include/
```

#### Step 4: Bundle npm Packages

You need to bundle opencode and kilo npm packages:

```bash
# Create a temporary directory
mkdir -p /tmp/codepal-packages
cd /tmp/codepal-packages

# Install packages
npm init -y
npm install opencode kilo

# Copy to assets
cp -r node_modules/opencode/* /path/to/app/src/main/assets/opencode/
cp -r node_modules/kilo/* /path/to/app/src/main/assets/kilo/
```

**Note**: This is a simplified approach. In production, you'd want to:
1. Run `npm ci --production` to get only production dependencies
2. Strip unnecessary files (docs, tests, etc.)
3. Minify JavaScript
4. Handle native dependencies carefully

## Directory Structure

After setup, your project should look like:

```
app/src/main/
├── cpp/
│   ├── CMakeLists.txt          # CMake build config
│   └── native-node.cpp         # JNI bridge
├── jniLibs/
│   ├── include/                # Node.js headers
│   │   └── node/
│   │       ├── v8.h
│   │       └── ...
│   ├── arm64-v8a/
│   │   ├── libnodejs-mobile.so
│   │   ├── libv8_libbase.so
│   │   └── libv8_libplatform.so
│   ├── armeabi-v7a/
│   │   └── ... (same files)
│   ├── x86/
│   │   └── ...
│   └── x86_64/
│       └── ...
└── assets/
    ├── opencode/               # Bundled opencode package
    │   ├── bin/
    │   └── lib/
    ├── kilo/                   # Bundled kilo package
    │   ├── bin/
    │   └── lib/
    └── workspace/              # Default project directory
```

## Build Configuration

The build is now configured in `build.gradle.kts`:

```kotlin
// NDK Configuration
ndk {
    abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
}

// CMake Configuration
externalNativeBuild {
    cmake {
        path = file("src/main/cpp/CMakeLists.txt")
        version = "3.22.1"
    }
}
```

## Building the Project

Once setup is complete:

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew :app:assembleDebug

# Or build specific ABI (faster for development)
./gradlew :app:assembleDebug -Pandroid.injected.build.abi=arm64-v8a
```

## Troubleshooting

### Error: "nodejs-mobile not found"

**Symptom**: Build fails with "nodejs-mobile not found for arm64-v8a"

**Solution**: The .so files aren't in the right place. Verify:
```bash
ls -la app/src/main/jniLibs/arm64-v8a/libnodejs-mobile.so
```

### Error: "UnsatisfiedLinkError: dlopen failed"

**Symptom**: App crashes when trying to load native library

**Solutions**:
1. Check that all required .so files are present for your device's architecture
2. Run `./gradlew clean` and rebuild
3. Check logcat for specific error: `adb logcat | grep dlopen`

### Error: "node_main not found"

**Symptom**: Native code compiles but Node.js doesn't start

**Solution**: The nodejs-mobile library is missing or corrupted. Re-download and extract.

### Slow Build Times

**Solution**: Build for only your target ABI during development:

```kotlin
ndk {
    // During development, build only for your device
    abiFilters += listOf("arm64-v8a")  // or your device's ABI
}
```

## Alternative: Without nodejs-mobile

If you can't or don't want to use nodejs-mobile, the app will:
- Gracefully disable the runtime features
- Show helpful error messages
- Still function as a file manager and WebView client

This is useful for development and testing UI components.

## Resources

- [nodejs-mobile GitHub](https://github.com/nodejs-mobile/nodejs-mobile)
- [nodejs-mobile React Native](https://github.com/nodejs-mobile/nodejs-mobile-react-native)
- [Android NDK Documentation](https://developer.android.com/ndk)
- [CMake for Android](https://developer.android.com/studio/projects/add-native-code)

## Architecture Support

| Architecture | Description | Typical Devices |
|-------------|-------------|-----------------|
| arm64-v8a | ARM 64-bit | Modern Android phones (2016+) |
| armeabi-v7a | ARM 32-bit | Older phones, some tablets |
| x86_64 | Intel 64-bit | Emulators, some tablets |
| x86 | Intel 32-bit | Legacy emulators |

**Recommendation**: Support all 4 for production, but arm64-v8a covers ~95% of modern devices.
