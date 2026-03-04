#!/bin/bash
# Simple emulator launcher with minimal resources

# Use home SDK where system images are installed
export ANDROID_SDK_ROOT="$HOME/android-sdk"
export ANDROID_HOME="$HOME/android-sdk"

EMULATOR="/opt/android-sdk/emulator/emulator"
AVD_NAME="kilo-emulator"

# Kill any existing adb
adb kill-server 2>/dev/null
sleep 2

# Launch with minimal settings
"$EMULATOR" -avd "$AVD_NAME" \
    -gpu swiftshader_indirect \
    -memory 2048 \
    -cores 1 \
    -skin 320x480 \
    -no-boot-anim \
    -no-snapshot \
    -no-audio \
    -qemu -cpu qemu64,-avx,-avx2,-sse4.2 "$@"
