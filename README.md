# CodePal (formerly Kilo Companion)

> **The first truly mobile-native AI coding environment.** No Termux. No remote servers. Just your Android device and the power of Node.js.

**A native Android companion app for OpenCode and Kilo CLI tools.**

CodePal provides a mobile-friendly interface for managing AI CLI tool configurations, handling OAuth authentication, and running AI coding assistants directly on your device - all without needing to use a terminal emulator.

---

## What Makes This Different

Unlike every other "AI coding assistant" on GitHub that's just a Termux wrapper or remote client, CodePal is:

- ✅ **Self-contained** - Embedded Node.js runtime via nodejs-mobile
- ✅ **Native Android** - Built with Jetpack Compose & Material 3
- ✅ **Offline capable** - Run AI coding entirely on your device
- ✅ **Professional quality** - Zero "slop", polished UX

---

## Features

- **Configuration Dashboard**: Manage OpenCode/Kilo config files in a user-friendly interface
- **OAuth Handler**: Automatically intercepts authentication redirects from CLI tools
- **WebView Wrapper**: Access local CLI web interfaces at `localhost:3000`
- **Runtime Management**: Start/stop embedded Node.js environment
- **Shared Workspace**: Uses `Documents/KiloWorkspace` for seamless CLI integration
- **Material Design 3**: Modern, responsive UI with light/dark theme support

---

## Architecture

```
CodePal
├── Android App (Kotlin + Compose)
│   ├── UI Layer - Material 3 interface
│   ├── Service Layer - Node.js runtime management
│   └── Native Layer - JNI bridge to Node.js
├── Embedded Node.js (nodejs-mobile)
├── Bundled Packages
│   ├── opencode - AI coding assistant
│   └── kilo - Alternative AI assistant
└── Local LLM Support (planned)
```

---

## Current Status

### ✅ Completed
- [x] Android app foundation (Kotlin + Compose)
- [x] Material 3 UI with 4-tab navigation
- [x] Config editor supporting both OpenCode and Kilo
- [x] JNI bridge architecture for Node.js integration
- [x] Foreground service for persistent Node.js runtime
- [x] Runtime management UI

### 🚧 In Progress
- [ ] Download and integrate nodejs-mobile binaries
- [ ] Bundle opencode/kilo npm packages
- [ ] Complete native bridge implementation
- [ ] File manager with git integration

### 📋 Planned
- [ ] Local LLM support (llama.cpp)
- [ ] Syntax highlighting editor
- [ ] Terminal emulator
- [ ] Project templates
- [ ] Sync with desktop

---

## Quick Start

### Prerequisites
- Linux x86-64 system (or Termux on Android for "fun" builds)
- JDK 17 or higher
- Android SDK
- NDK (for native code)

### Build & Install

```bash
# 1. Install prerequisites (one-time setup)
./install-prerequisites.sh

# 2. Download Node.js Mobile binaries
./setup-nodejs-mobile.sh

# 3. Build the APK
./build-android.sh debug

# 4. Install to connected device
adb install -r kilo-companion-app/app/build/outputs/apk/debug/app-debug.apk
```

For detailed instructions, see:
- [BUILD.md](BUILD.md) - Complete build instructions
- [TERMUX.md](TERMUX.md) - Building on Android via Termux
- [USAGE.md](USAGE.md) - User guide

---

## Project Structure

```
.
├── AGENTS.md                    # Architecture guide
├── auto-git.sh                  # Automated git commits every 60s
├── build-android.sh             # Linux x86-64 build script
├── termux-build.sh              # Android ARM64 build script
├── install-prerequisites.sh     # Environment setup
├── setup-nodejs-mobile.sh       # Download Node.js binaries
├── kilo-companion-app/          # Main Android project
│   ├── app/src/main/
│   │   ├── cpp/                 # Native code (JNI)
│   │   ├── java/                # Kotlin source code
│   │   ├── assets/              # Bundled packages
│   │   └── res/                 # Android resources
│   └── build.gradle.kts         # Build config
└── docs/                        # User documentation
```

---

## Development

This project is designed for LLM-led development with:
- Comprehensive inline documentation
- Clear separation of concerns
- Industry-standard patterns (MVVM, Repository pattern)
- Automated git tracking via `auto-git.sh`

See [AGENTS.md](AGENTS.md) for architecture guide.

---

## License

MIT License - See [LICENSE](LICENSE) for details.

---

**Built with Kotlin, Jetpack Compose, and 🤖 AI assistance.**

**Built with ❤️ for mobile developers who want real power in their pockets.**

---

## Features

- **Configuration Dashboard**: Manage OpenCode/Kilo config files in a user-friendly interface
- **OAuth Handler**: Automatically intercepts authentication redirects from CLI tools
- **WebView Wrapper**: Access local CLI web interfaces at `localhost:3000`
- **Shared Workspace**: Uses `Documents/KiloWorkspace` for seamless CLI integration
- **Material Design 3**: Modern, responsive UI with light/dark theme support

---

## Quick Start

### Prerequisites
- Linux x86-64 system (or Termux on Android for "fun" builds)
- JDK 17 or higher
- Android SDK

### Build & Install

```bash
# 1. Install prerequisites (one-time setup)
./install-prerequisites.sh

# 2. Build the APK
./build-android.sh debug

# 3. Install to connected device
adb install -r kilo-companion-app/app/build/outputs/apk/debug/app-debug.apk
```

For detailed instructions, see:
- [BUILD.md](BUILD.md) - Complete build instructions
- [TERMUX.md](TERMUX.md) - Building on Android via Termux
- [USAGE.md](USAGE.md) - User guide

---

## Architecture

```
Kilo Companion (Android App)
├── Configuration Manager (JSON/JSONC editor)
├── OAuth Handler (Intent interceptor)
├── WebView (Localhost wrapper)
└── Shared Storage (Documents/KiloWorkspace)
         ↕
    OpenCode/Kilo CLI (in Termux or background)
```

---

## Project Structure

```
.
├── auto-git.sh                  # Automated git commits every 60s
├── build-android.sh             # Linux x86-64 build script
├── termux-build.sh              # Android ARM64 build script
├── install-prerequisites.sh     # Environment setup
├── kilo-companion-app/          # Main Android project
│   ├── app/src/main/java/       # Kotlin source code
│   │   ├── MainActivity.kt
│   │   ├── AuthHandlerActivity.kt
│   │   ├── ui/screens/          # Compose screens
│   │   └── data/                # SharedStorageManager
│   └── app/src/main/res/        # Android resources
├── AGENT_SCRATCH/               # LLM development documentation
│   ├── AGENTS.md
│   ├── DECISIONS.md
│   └── WORKFLOW.md
└── docs/                        # User documentation
```

---

## Development

This project is designed for LLM-led development with:
- Comprehensive inline documentation
- Clear separation of concerns
- Industry-standard patterns (MVVM, Repository pattern)
- Automated git tracking via `auto-git.sh`

See [AGENT_SCRATCH/AGENTS.md](AGENT_SCRATCH/AGENTS.md) for LLM agent coordination.

---

## License

MIT License - See [LICENSE](LICENSE) for details.

---

**Built with Kotlin, Jetpack Compose, and 🤖 AI assistance.**
