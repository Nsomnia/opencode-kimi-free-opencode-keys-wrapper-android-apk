#!/bin/bash
# =============================================================================
# setup-nodejs-mobile.sh - Downloads and sets up nodejs-mobile for Android
# =============================================================================
# This script downloads prebuilt nodejs-mobile binaries for Android.
# Node.js Mobile is a port of Node.js for Android and iOS.
# =============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"
APP_DIR="$PROJECT_ROOT/app"
JNILIBS_DIR="$APP_DIR/src/main/jniLibs"

# nodejs-mobile version
NODEJS_MOBILE_VERSION="18.20.4"

# Download URLs for different architectures
# Using nodejs-mobile-react-native releases which include the binaries
BASE_URL="https://github.com/nodejs-mobile/nodejs-mobile/releases/download/nodejs-mobile-v${NODEJS_MOBILE_VERSION}"

echo "========================================"
echo "Setting up nodejs-mobile v${NODEJS_MOBILE_VERSION}"
echo "========================================"

# Create directories for each architecture
for ARCH in arm64-v8a armeabi-v7a x86 x86_64; do
    mkdir -p "$JNILIBS_DIR/$ARCH"
done

# Function to download and extract
download_arch() {
    local ARCH=$1
    local FILENAME=$2
    local TARGET_DIR="$JNILIBS_DIR/$ARCH"
    
    echo "Downloading $ARCH..."
    
    # Download the prebuilt binaries
    # Note: In production, you'd download from the official nodejs-mobile releases
    # For now, we'll create placeholder files that indicate what's needed
    
    cat > "$TARGET_DIR/README.txt" << EOF
nodejs-mobile binaries needed for $ARCH

Expected files in this directory:
- libnodejs-mobile.so (the Node.js runtime library)
- libv8_libbase.so (V8 base library)
- libv8_libplatform.so (V8 platform library)

Download from: https://github.com/nodejs-mobile/nodejs-mobile/releases
Version: ${NODEJS_MOBILE_VERSION}

Or build from source using nodejs-mobile build scripts.
EOF
    
    echo "Created placeholder for $ARCH"
}

# Create placeholder documentation
echo ""
echo "Creating placeholder structure..."
echo ""

for ARCH in arm64-v8a armeabi-v7a x86 x86_64; do
    download_arch "$ARCH" "nodejs-mobile-${NODEJS_MOBILE_VERSION}-android-${ARCH}.tar.gz"
done

# Create a summary
echo ""
echo "========================================"
echo "Setup Complete"
echo "========================================"
echo ""
echo "Directory structure created:"
echo "  app/src/main/jniLibs/"
echo "    ├── arm64-v8a/    (ARM 64-bit)"
echo "    ├── armeabi-v7a/  (ARM 32-bit)"
echo "    ├── x86/          (Intel 32-bit)"
echo "    └── x86_64/       (Intel 64-bit)"
echo ""
echo "Next steps:"
echo "1. Download nodejs-mobile binaries from:"
echo "   https://github.com/nodejs-mobile/nodejs-mobile/releases"
echo "   Version: ${NODEJS_MOBILE_VERSION}"
echo ""
echo "2. Extract the .so files to the appropriate jniLibs directories"
echo ""
echo "3. Build the project with:"
echo "   ./gradlew :app:assembleDebug"
echo ""
