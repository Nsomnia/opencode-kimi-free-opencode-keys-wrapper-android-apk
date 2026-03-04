#!/bin/bash
# =============================================================================
# setup-nodejs-mobile.sh - Download and setup nodejs-mobile binaries
# =============================================================================
# This script downloads prebuilt nodejs-mobile binaries for Android
# =============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="$SCRIPT_DIR/kilo-companion-app/app"
JNI_LIBS_DIR="$APP_DIR/src/main/jniLibs"
ASSETS_DIR="$APP_DIR/src/main/assets"

NODEJS_VERSION="18.20.4"
NODEJS_MOBILE_URL="https://github.com/nodejs-mobile/nodejs-mobile/releases/download/v${NODEJS_VERSION}/nodejs-mobile-v${NODEJS_VERSION}-android.zip"

echo "========================================"
echo "  Setting up Node.js Mobile"
echo "========================================"
echo "Version: $NODEJS_VERSION"
echo ""

# Create directories
mkdir -p "$JNI_LIBS_DIR"
mkdir -p "$ASSETS_DIR/nodejs"

cd /tmp

# Download nodejs-mobile if not present
if [ ! -f "nodejs-mobile.zip" ]; then
    echo "[INFO] Downloading nodejs-mobile..."
    wget -q --show-progress "$NODEJS_MOBILE_URL" -O nodejs-mobile.zip || {
        echo "[ERROR] Failed to download nodejs-mobile"
        echo "[INFO] You can manually download from:"
        echo "       https://github.com/nodejs-mobile/nodejs-mobile/releases"
        exit 1
    }
fi

# Extract
echo "[INFO] Extracting nodejs-mobile..."
unzip -q nodejs-mobile.zip -d nodejs-mobile-extract || {
    echo "[ERROR] Failed to extract nodejs-mobile"
    exit 1
}

# Setup architectures
for ARCH in arm64-v8a armeabi-v7a x86_64 x86; do
    echo "[INFO] Setting up $ARCH..."
    
    mkdir -p "$JNI_LIBS_DIR/$ARCH"
    
    # Copy prebuilt binaries if they exist
    if [ -d "nodejs-mobile-extract/lib/$ARCH" ]; then
        cp -r nodejs-mobile-extract/lib/$ARCH/* "$JNI_LIBS_DIR/$ARCH/" 2>/dev/null || true
    fi
    
    # Copy headers (shared across architectures)
    if [ -d "nodejs-mobile-extract/include" ] && [ ! -d "$JNI_LIBS_DIR/include" ]; then
        cp -r nodejs-mobile-extract/include "$JNI_LIBS_DIR/"
    fi
done

# Copy Node.js binary to assets
if [ -f "nodejs-mobile-extract/bin/node" ]; then
    cp nodejs-mobile-extract/bin/node "$ASSETS_DIR/nodejs/node"
    chmod +x "$ASSETS_DIR/nodejs/node"
fi

# Cleanup
rm -rf nodejs-mobile-extract

echo ""
echo "[SUCCESS] Node.js Mobile setup complete!"
echo ""
echo "Next steps:"
echo "1. Download opencode and kilo npm packages"
echo "2. Place them in app/src/main/assets/"
echo "3. Build the app: ./build-android.sh"
