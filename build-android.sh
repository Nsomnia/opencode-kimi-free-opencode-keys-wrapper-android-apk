#!/bin/bash
# =============================================================================
# Build Script for Linux x86-64
# =============================================================================
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

BUILD_TYPE="${1:-debug}"
PROJECT_DIR="kilo-companion-app"

print_status() { echo -e "${BLUE}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }

check_java() {
    if ! command -v java &> /dev/null; then
        print_error "Java not installed. Install JDK 17+: sudo apt install openjdk-17-jdk"
        exit 1
    fi
}

check_android_sdk() {
    if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
        print_error "ANDROID_HOME not set. Run: ./install-prerequisites.sh"
        exit 1
    fi
}

build_apk() {
    cd "$PROJECT_DIR"
    print_status "Building APK (this may take a few minutes)..."
    ./gradlew "assemble${BUILD_TYPE^}" --no-daemon
    cd ..
    
    APK_PATH="$PROJECT_DIR/app/build/outputs/apk/$BUILD_TYPE/app-${BUILD_TYPE}.apk"
    if [ -f "$APK_PATH" ]; then
        print_success "Build complete!"
        echo ""
        echo "APK: $APK_PATH"
        echo "Install: adb install -r $APK_PATH"
    fi
}

echo "========================================"
echo "  Kilo Companion Build - $BUILD_TYPE"
echo "========================================"
check_java
check_android_sdk
build_apk
print_success "Done!"
