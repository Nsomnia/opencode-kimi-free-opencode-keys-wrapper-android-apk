# Building on Android with Termux

Build the Kilo Companion APK directly on your Android device using Termux!

## Install Termux
Download from [F-Droid](https://f-droid.org/packages/com.termux/)

## Setup
```bash
pkg update
pkg install openjdk-17 gradle git
termux-setup-storage
```

## Build
```bash
cd ~/storage/shared/Documents/kilo-companion
./termux-build.sh debug
```

**Warning**: Takes 10-30 minutes on mobile ARM64!

## Transfer APK
```bash
cp kilo-companion-app/app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/
```

For detailed instructions, see full documentation in the repository.
