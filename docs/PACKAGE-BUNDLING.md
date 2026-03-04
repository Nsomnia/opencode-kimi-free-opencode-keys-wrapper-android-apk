# Package Bundling Guide

## What You Need

**npm packages are JavaScript source code** (not compiled binaries). You need to bundle the actual package source so the embedded Node.js can execute it.

## Option 1: From npm (if public packages)

If opencode and kilo are public npm packages:

```bash
./bundle-packages.sh
```

This will:
1. Run `npm install opencode kilo`
2. Copy `node_modules/` to `app/src/main/assets/packages/`
3. Clean up unnecessary files (docs, tests, etc.)

## Option 2: From Global Installation (if already installed)

If you already have them installed globally:

```bash
# Find where npm installed them globally
npm root -g

# Example output: /usr/lib/node_modules or ~/.nvm/versions/node/v20.x/lib/node_modules

# Copy to app assets
mkdir -p kilo-companion-app/app/src/main/assets/packages/node_modules
cp -r $(npm root -g)/opencode kilo-companion-app/app/src/main/assets/packages/node_modules/
cp -r $(npm root -g)/kilo kilo-companion-app/app/src/main/assets/packages/node_modules/
```

## Option 3: From Source (if GitHub repos)

If these are private/internal tools from GitHub:

```bash
# Clone the repositories
cd /tmp
git clone https://github.com/organization/opencode.git
git clone https://github.com/organization/kilo.git

# Install dependencies
cd opencode && npm install && cd ..
cd kilo && npm install && cd ..

# Copy to app assets
mkdir -p kilo-companion-app/app/src/main/assets/packages/node_modules
cp -r opencode kilo-companion-app/app/src/main/assets/packages/node_modules/
cp -r kilo kilo-companion-app/app/src/main/assets/packages/node_modules/
```

## Option 4: From Your Existing Installation

You mentioned they're already installed. Find them:

```bash
# Find opencode
which opencode
# Example: /usr/bin/opencode

# Find the actual package directory
ls -la $(dirname $(which opencode))/../lib/node_modules/opencode

# Or check these common locations:
ls ~/.npm-global/lib/node_modules/
ls /usr/lib/node_modules/
ls ~/.nvm/versions/node/*/lib/node_modules/
ls ~/.local/share/pnpm/global/*/node_modules/
ls ~/.bun/install/global/node_modules/
```

Once you find them:
```bash
mkdir -p kilo-companion-app/app/src/main/assets/packages/node_modules
cp -r /path/to/opencode kilo-companion-app/app/src/main/assets/packages/node_modules/
cp -r /path/to/kilo kilo-companion-app/app/src/main/assets/packages/node_modules/
```

## What Gets Bundled

The bundle includes:
- ✅ JavaScript source files (.js)
- ✅ Package metadata (package.json)
- ✅ Dependencies (other npm packages they need)
- ❌ Documentation (.md files)
- ❌ Tests and examples
- ❌ TypeScript source (if compiled to JS)

## Size Considerations

npm packages can be large due to dependencies:
- Simple package: ~1-5MB
- Complex CLI tool: ~20-100MB
- With all dependencies: ~100-500MB

The `bundle-packages.sh` script removes unnecessary files to minimize APK size.

## Testing

After bundling, check:

```bash
# See what was bundled
ls -la kilo-companion-app/app/src/main/assets/packages/node_modules/

# Check package.json exists
cat kilo-companion-app/app/src/main/assets/packages/node_modules/opencode/package.json

# Check main entry point exists
ls kilo-companion-app/app/src/main/assets/packages/node_modules/opencode/bin/
```

## Troubleshooting

**"Cannot find module" errors**: The package has dependencies that weren't copied. Make sure you're copying the entire `node_modules` directory.

**Native module errors**: If the packages use native C++ modules, they won't work without being compiled for Android ARM. These need special handling.

**Size too large**: Use the cleanup commands in `bundle-packages.sh` to remove docs, tests, and source maps.

## Questions?

Where are your opencode and kilo packages currently installed? Run:
```bash
npm list -g opencode kilo 2>/dev/null || echo "Not installed via npm"
which opencode
which kilo
```

This will help determine which option above to use.
