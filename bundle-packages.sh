#!/bin/bash
# =============================================================================
# bundle-packages.sh - Download and bundle opencode & kilo npm packages
# =============================================================================
# This script downloads npm packages and prepares them for bundling in the app.
# Note: These are JavaScript packages, not compiled binaries.
# =============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="$SCRIPT_DIR/kilo-companion-app/app"
ASSETS_DIR="$APP_DIR/src/main/assets"
PACKAGES_DIR="$ASSETS_DIR/packages"

echo "========================================"
echo "  Bundling NPM Packages"
echo "========================================"
echo ""

# Create directories
mkdir -p "$PACKAGES_DIR"

cd /tmp

# Check if npm is available
if ! command -v npm &> /dev/null; then
    echo "[ERROR] npm not found. Please install Node.js first."
    echo "        On Arch: sudo pacman -S nodejs npm"
    exit 1
fi

# Create a temporary package.json for clean install
cat > package.json << 'EOF'
{
  "name": "codepal-packages",
  "version": "1.0.0",
  "dependencies": {}
}
EOF

echo "[INFO] Installing opencode..."
npm install opencode-ai --save 2>&1 | grep -v "npm WARN" || true

echo "[INFO] Installing kilo..."
npm install kilo --save 2>&1 | grep -v "npm WARN" || true

# Check if packages were installed
if [ ! -d "node_modules/opencode" ]; then
    echo "[ERROR] Failed to install opencode"
    echo "[INFO] If these are private packages, you may need to:"
    echo "       1. Clone the source from GitHub"
    echo "       2. Build them manually"
    echo "       3. Place in $PACKAGES_DIR/"
    exit 1
fi

if [ ! -d "node_modules/kilo" ]; then
    echo "[ERROR] Failed to install kilo"
    exit 1
fi

# Copy packages to assets
echo "[INFO] Copying packages to assets..."
rm -rf "$PACKAGES_DIR"/*
cp -r node_modules "$PACKAGES_DIR/"

# Clean up unnecessary files to reduce APK size
echo "[INFO] Cleaning up unnecessary files..."
cd "$PACKAGES_DIR"

# Remove files that aren't needed at runtime
find . -type f \( \
    -name "*.md" -o \
    -name "*.markdown" -o \
    -name "LICENSE" -o \
    -name "LICENSE.txt" -o \
    -name ".gitignore" -o \
    -name ".npmignore" -o \
    -name "*.ts" -o \
    -name "*.map" -o \
    -name "*.test.js" -o \
    -name "*.spec.js" -o \
    -name "__tests__" -o \
    -name "test" -o \
    -name "tests" -o \
    -name "docs" -o \
    -name "examples" -o \
    -name ".github" \
\) -delete 2>/dev/null || true

# Remove empty directories
find . -type d -empty -delete 2>/dev/null || true

echo ""
echo "[SUCCESS] Packages bundled successfully!"
echo ""
echo "Bundled packages:"
echo "  - opencode"
echo "  - kilo"
echo "  - $(find node_modules -maxdepth 1 -type d | wc -l) total dependencies"
echo ""
echo "Location: $PACKAGES_DIR"
echo ""
echo "Next steps:"
echo "1. Run: ./setup-nodejs-mobile.sh"
echo "2. Build the app: ./build-android.sh"

# Cleanup temp files
cd "$SCRIPT_DIR"
rm -rf /tmp/package.json /tmp/package-lock.json /tmp/node_modules 2>/dev/null || true
