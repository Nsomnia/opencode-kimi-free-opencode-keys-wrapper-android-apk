#!/bin/bash
# =============================================================================
# launch-emulator.sh
# =============================================================================
# Script to setup and launch Android emulator without Android Studio
# =============================================================================

set -e

# Configuration
API_LEVEL=34
ARCH=x86_64
AVD_NAME="kilo-emulator"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check environment
check_env() {
    print_info "Checking Android SDK environment..."
    
    # Prefer home SDK over system SDK (newer cmdline-tools work with Java 17)
    if [ -d "$HOME/android-sdk" ]; then
        export ANDROID_SDK_ROOT="$HOME/android-sdk"
        export ANDROID_HOME="$HOME/android-sdk"
        print_info "Using home SDK at: $ANDROID_SDK_ROOT"
    elif [ -z "$ANDROID_SDK_ROOT" ] && [ -z "$ANDROID_HOME" ]; then
        export ANDROID_SDK_ROOT=/opt/android-sdk
        export ANDROID_HOME=/opt/android-sdk
        print_info "Set ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT"
    fi
    
    SDK_ROOT=${ANDROID_SDK_ROOT:-$ANDROID_HOME}
    
    if [ ! -d "$SDK_ROOT" ]; then
        print_error "Android SDK not found at $SDK_ROOT"
        exit 1
    fi
    
    print_info "Using SDK at: $SDK_ROOT"
}

# Install system image
install_system_image() {
    print_info "Installing system image for API $API_LEVEL..."
    
    SDKMANAGER="$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"
    
    if [ -f "$SDKMANAGER" ]; then
        "$SDKMANAGER" "system-images;android-$API_LEVEL;google_apis;$ARCH"
    else
        print_error "Could not find sdkmanager at $SDKMANAGER"
        print_info "You may need to install cmdline-tools:"
        print_info "  https://developer.android.com/studio/command-line"
        exit 1
    fi
    
    print_info "System image installed successfully"
}

# Create AVD
create_avd() {
    print_info "Creating AVD: $AVD_NAME..."
    
    AVDMANAGER="$SDK_ROOT/cmdline-tools/latest/bin/avdmanager"
    
    if [ -f "$AVDMANAGER" ]; then
        "$AVDMANAGER" create avd \
            -n "$AVD_NAME" \
            -k "system-images;android-$API_LEVEL;google_apis;$ARCH" \
            --force
    else
        print_error "Could not find avdmanager at $AVDMANAGER"
        exit 1
    fi
    
    print_info "AVD created successfully"
}

# Check if AVD exists
avd_exists() {
    if [ -d "$HOME/.android/avd/$AVD_NAME.avd" ]; then
        return 0
    else
        return 1
    fi
}

# Launch emulator
launch_emulator() {
    print_info "Launching emulator..."
    
    # Use system emulator but with home SDK for system images
    export ANDROID_SDK_ROOT="$HOME/android-sdk"
    export ANDROID_HOME="$HOME/android-sdk"
    EMULATOR="/opt/android-sdk/emulator/emulator"
    
    if [ ! -f "$EMULATOR" ]; then
        print_error "Emulator not found"
        exit 1
    fi
    
    print_info "Starting $AVD_NAME..."
    print_info "Emulator window should appear shortly..."
    
    # Launch with compatibility flags for older CPUs
    # -no-accel: Disable hardware acceleration (use if KVM/CPU issues)
    # -gpu swiftshader: Software rendering (slower but compatible)
    # -cores 2: Match KVM capability
    # -memory 3072: More RAM for smoother boot
    "$EMULATOR" -avd "$AVD_NAME" \
        -gpu swiftshader_indirect \
        -memory 3072 \
        -cores 2 \
        -skin 480x800 \
        -no-boot-anim \
        -no-snapshot \
        -qemu -cpu qemu64,-avx,-avx2 &
    
    EMULATOR_PID=$!
    
    print_info "Emulator launched with PID: $EMULATOR_PID"
    print_info "Waiting for Android to boot (this may take 1-2 minutes)..."
    print_info "Do not click on the window until the home screen appears!"
    
    # Wait for adb daemon to start
    sleep 10
    adb wait-for-device
    
    # Wait for Android to fully boot (check for sys.boot_completed)
    print_info "Device connected, waiting for Android OS to finish loading..."
    while [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" != "1" ]; do
        sleep 2
        echo -n "."
    done
    echo ""
    
    print_info "Android is ready!"
    print_info ""
    print_info "Useful commands:"
    print_info "  adb devices          - List connected devices"
    print_info "  adb logcat           - View device logs"
    print_info "  adb install app.apk  - Install APK"
    print_info ""
    print_info "To stop emulator: kill $EMULATOR_PID"
}

# List available AVDs
list_avds() {
    print_info "Available AVDs:"
    
    AVDMANAGER="$SDK_ROOT/cmdline-tools/latest/bin/avdmanager"
    
    if [ -f "$AVDMANAGER" ]; then
        "$AVDMANAGER" list avd
    else
        ls -la ~/.android/avd/ 2>/dev/null | grep "\.avd" || print_warn "No AVDs found"
    fi
}

# Main menu
show_menu() {
    echo ""
    echo "====================================="
    echo "  Android Emulator Manager"
    echo "====================================="
    echo ""
    echo "1) Setup and launch emulator (first time)"
    echo "2) Launch existing emulator"
    echo "3) List available AVDs"
    echo "4) Install APK to emulator"
    echo "5) Install system image only"
    echo "6) Exit"
    echo ""
    read -p "Select option [1-6]: " choice
    
    case $choice in
        1)
            check_env
            if ! avd_exists; then
                install_system_image
                create_avd
            fi
            launch_emulator
            ;;
        2)
            check_env
            if avd_exists; then
                launch_emulator
            else
                print_error "No AVD found. Run option 1 first."
            fi
            ;;
        3)
            check_env
            list_avds
            show_menu
            ;;
        4)
            check_env
            print_info "Installing APK to emulator..."
            APK_PATH="kilo-companion-app/app/build/outputs/apk/debug/app-debug.apk"
            if [ ! -f "$APK_PATH" ]; then
                print_error "APK not found at: $APK_PATH"
                print_info "Build first with: cd kilo-companion-app && ./gradlew assembleDebug"
            else
                adb install -r "$APK_PATH"
                print_info "APK installed successfully!"
            fi
            show_menu
            ;;
        5)
            check_env
            install_system_image
            ;;
        6)
            exit 0
            ;;
        *)
            print_error "Invalid option"
            show_menu
            ;;
    esac
}

# Handle command line arguments
if [ $# -eq 0 ]; then
    show_menu
else
    case "$1" in
        setup)
            check_env
            install_system_image
            create_avd
            ;;
        launch)
            check_env
            launch_emulator
            ;;
        list)
            check_env
            list_avds
            ;;
        install)
            check_env
            print_info "Installing APK to emulator..."
            APK_PATH="${2:-kilo-companion-app/app/build/outputs/apk/debug/app-debug.apk}"
            if [ ! -f "$APK_PATH" ]; then
                print_error "APK not found at: $APK_PATH"
                print_info "Build first with: ./gradlew assembleDebug"
                exit 1
            fi
            adb install -r "$APK_PATH"
            print_info "APK installed successfully!"
            ;;
        *)
            echo "Usage: $0 [setup|launch|list|install]"
            echo ""
            echo "Commands:"
            echo "  setup   - Install system image and create AVD"
            echo "  launch  - Launch the emulator"
            echo "  list    - List available AVDs"
            echo "  install [path] - Install APK to emulator (optional: path to APK)"
            echo ""
            echo "Or run without arguments for interactive menu."
            exit 1
            ;;
    esac
fi
