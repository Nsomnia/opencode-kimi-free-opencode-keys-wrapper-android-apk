# Kilo Companion

**A native Android companion app for OpenCode and Kilo CLI tools.**

Kilo Companion provides a mobile-friendly interface for managing AI CLI tool configurations, handling OAuth authentication, and accessing local web interfaces - all without needing to use a terminal emulator on your phone.

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
