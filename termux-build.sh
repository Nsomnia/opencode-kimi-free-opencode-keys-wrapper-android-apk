#!/bin/bash
# =============================================================================
# Build Script for Termux (Android ARM64)
# =============================================================================
set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

PROJECT_DIR="kilo-companion-app"
BUILD_TYPE="${1:-debug}"

print_status() { echo -e "${BLUE}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARN]${NC} $1"; }

print_warning "Building on mobile ARM64. This will take significantly longer!"
echo ""

build() {
    cd "$PROJECT_DIR"
    print_status "Building in Termux..."
    gradle "assemble${BUILD_TYPE^}"
    cd ..
    
    APK_PATH="$PROJECT_DIR/app/build/outputs/apk/$BUILD_TYPE/app-${BUILD_TYPE}.apk"
    if [ -f "$APK_PATH" ]; then
        print_success "Build complete! APK: $APK_PATH"
    fi
}

echo "========================================"
echo "  Termux Build - Kilo Companion"
echo "========================================"
build
print_success "Finished!"
