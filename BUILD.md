# Build Instructions

Complete guide for building Kilo Companion APK on Linux x86-64.

## Prerequisites

### System Requirements
- Linux x86-64 (Ubuntu 20.04+, Fedora 35+, or equivalent)
- 4GB RAM minimum (8GB recommended)
- 10GB free disk space

### Required Software
1. **JDK 17 or higher**
2. **Android SDK** (API 34, Build Tools 34.0.0)
3. **Git**

## One-Time Setup

### Option A: Automated Setup
```bash
./install-prerequisites.sh
```

### Option B: Manual Setup

#### 1. Install JDK 17

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**Fedora:**
```bash
sudo dnf install java-17-openjdk-devel
```

**Arch:**
```bash
sudo pacman -S jdk17-openjdk
```

#### 2. Install Android SDK

```bash
# Create SDK directory
mkdir -p ~/android-sdk
cd ~/android-sdk

# Download command line tools
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip commandlinetools-linux-*.zip
mkdir -p cmdline-tools/latest
mv cmdline-tools/lib cmdline-tools/bin cmdline-tools/latest/

# Install required components
export ANDROID_HOME=~/android-sdk
yes | cmdline-tools/latest/bin/sdkmanager --licenses
cmdline-tools/latest/bin/sdkmanager "platforms;android-34" "build-tools;34.0.0"
```

#### 3. Set Environment Variables

Add to `~/.bashrc` or `~/.zshrc`:
```bash
export ANDROID_HOME=$HOME/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
```

Reload:
```bash
source ~/.bashrc  # or ~/.zshrc
```

## Building the APK

### Debug Build (Recommended for Development)
```bash
./build-android.sh debug
```

Output: `kilo-companion-app/app/build/outputs/apk/debug/app-debug.apk`

### Release Build
```bash
./build-android.sh release
```

Output: `kilo-companion-app/app/build/outputs/apk/release/app-release-unsigned.apk`

## Installing the APK

### Via ADB (USB Debugging)
```bash
# Connect device with USB debugging enabled
adb devices  # Verify device is connected
adb install -r kilo-companion-app/app/build/outputs/apk/debug/app-debug.apk
```

### Manual Installation
1. Copy APK to device (USB, cloud storage, etc.)
2. On device: Enable "Install from unknown sources" in Settings
3. Tap the APK file to install

## Troubleshooting

### Build Fails: "Could not find tools.jar"
JDK is not properly installed. Reinstall JDK 17.

### Build Fails: "SDK location not found"
`ANDROID_HOME` is not set. Run:
```bash
export ANDROID_HOME=$HOME/android-sdk
```

### Out of Memory
Increase Gradle heap size in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m
```

### Gradle Daemon Issues
```bash
cd kilo-companion-app
./gradlew --stop
```

## Verifying the Build

Check APK was created:
```bash
ls -lh kilo-companion-app/app/build/outputs/apk/debug/
```

Analyze APK contents:
```bash
cd kilo-companion-app
./gradlew app:analyzeDebugBundle
```

## Continuous Integration

For CI/CD pipelines:
```bash
# Non-interactive build
export ANDROID_HOME=/opt/android-sdk
./build-android.sh release
```
