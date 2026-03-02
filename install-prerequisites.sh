#!/bin/bash
# =============================================================================
# Prerequisites Installer for Linux x86-64
# =============================================================================
set -e

BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_status() { echo -e "${BLUE}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARN]${NC} $1"; }

ANDROID_SDK_DIR="$HOME/android-sdk"

install_java() {
    print_status "Installing OpenJDK 17..."
    if command -v apt &> /dev/null; then
        sudo apt update && sudo apt install -y openjdk-17-jdk
    elif command -v dnf &> /dev/null; then
        sudo dnf install -y java-17-openjdk-devel
    else
        print_warning "Please install JDK 17 manually"
        exit 1
    fi
}

install_android_sdk() {
    print_status "Installing Android SDK..."
    mkdir -p "$ANDROID_SDK_DIR"
    cd "$ANDROID_SDK_DIR"
    
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
    unzip -q commandlinetools-linux-*.zip
    mkdir -p cmdline-tools/latest
    mv cmdline-tools/lib cmdline-tools/latest/ 2>/dev/null || true
    mv cmdline-tools/bin cmdline-tools/latest/ 2>/dev/null || true
    rm commandlinetools-linux-*.zip
    
    export ANDROID_HOME="$ANDROID_SDK_DIR"
    yes | cmdline-tools/latest/bin/sdkmanager --licenses || true
    cmdline-tools/latest/bin/sdkmanager "platforms;android-34" "build-tools;34.0.0"
    
    print_success "Android SDK installed"
}

configure_env() {
    SHELL_RC="$HOME/.bashrc"
    [ -f "$HOME/.zshrc" ] && SHELL_RC="$HOME/.zshrc"
    
    if ! grep -q "ANDROID_HOME" "$SHELL_RC"; then
        echo "" >> "$SHELL_RC"
        echo "export ANDROID_HOME=$ANDROID_SDK_DIR" >> "$SHELL_RC"
        echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin' >> "$SHELL_RC"
    fi
    
    print_warning "Restart terminal or run: source $SHELL_RC"
}

echo "========================================"
echo "  Installing Prerequisites"
echo "========================================"

if ! command -v java &> /dev/null; then
    install_java
fi

if [ ! -d "$ANDROID_SDK_DIR" ]; then
    install_android_sdk
    configure_env
fi

print_success "Complete!"
echo "Run: ./build-android.sh"
