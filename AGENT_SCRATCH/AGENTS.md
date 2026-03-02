# AGENTS.md - LLM Agent Development Guide

## Project: Kilo Companion Android App

**Purpose**: This document serves as the central coordination hub for LLM agents working on this project. It contains architectural decisions, development workflows, and agent-specific instructions.

---

## Project Overview

This is a native Android "Companion Wrapper" application for OpenCode and Kilo CLI tools. The app acts as a:
- Configuration Dashboard
- Authentication Handler
- Web Wrapper for local CLI interfaces

### Key Design Decisions

1. **No Native Node Execution**: The app does NOT embed Node.js. It manages configuration files and provides UI wrappers.
2. **Shared Storage Pattern**: Uses `Documents/KiloWorkspace` as a shared directory accessible by both the Android app and Termux/CLI.
3. **Jetpack Compose UI**: Modern declarative UI framework for Android.
4. **OAuth Intent Handling**: Custom URI scheme interception for seamless auth flows.

---

## Directory Structure

```
/workspace/
├── AGENT_SCRATCH/          # LLM agent workspace and documentation
│   ├── AGENTS.md          # This file - master agent guide
│   ├── DECISIONS.md       # Architecture decision records
│   ├── PROGRESS.md        # Development progress log
│   └── WORKFLOW.md        # Development workflow documentation
├── kilo-companion-app/    # Main Android project
│   ├── app/               # Android app module
│   ├── scripts/           # Build and utility scripts
│   └── gradle/            # Gradle wrapper
├── docs/                  # Project documentation
│   └── PROJECT-INIT-PROMPT.md  # Original requirements
├── build-android.sh       # Linux x86-64 build script
├── termux-build.sh        # Android arm64 build script
├── install-prerequisites.sh  # Dependency installer
└── auto-git.sh            # Automated git commit script
```

---

## Development Workflow

### 1. Automated Git Tracking

Run the auto-git script in a separate terminal:
```bash
./auto-git.sh
```

This commits changes every 60 seconds with timestamps.

### 2. File Naming Conventions

- Kotlin files: `PascalCase.kt`
- XML resources: `snake_case.xml`
- Scripts: `kebab-case.sh`
- Documentation: `UPPERCASE.md` for important docs, `PascalCase.md` for guides

### 3. Code Organization

```kotlin
// Package structure
com.kilo.companion
├── data/          # Data models and file I/O
├── ui/
│   ├── screens/   # Full-screen Composables
│   ├── components/# Reusable UI components
│   └── theme/     # Colors, typography, shapes
└── utils/         # Utility functions
```

### 4. Comment Standards

All code must include:
- File-level documentation explaining purpose
- Function-level KDoc comments
- Inline comments for complex logic
- Permission handling explanations

Example:
```kotlin
/**
 * SharedStorageManager.kt
 * 
 * Manages file operations in the shared workspace directory.
 * Handles permission requests and file I/O for configuration files.
 * 
 * @author Kilo Companion Team
 * @since 1.0.0
 */
```

---

## Build Instructions

### Linux x86-64 (Standard Build)

```bash
# Install prerequisites
./install-prerequisites.sh

# Build debug APK
./build-android.sh debug

# Build release APK
./build-android.sh release
```

### Termux on Android (arm64)

```bash
# In Termux
./termux-build.sh
```

See `TERMUX.md` for detailed mobile development instructions.

---

## Testing Checklist

Before marking a task complete:
- [ ] Code compiles without warnings
- [ ] All permissions properly declared
- [ ] Error handling implemented
- [ ] Comments added for complex logic
- [ ] Auto-git is running
- [ ] Documentation updated

---

## Common Issues & Solutions

### Issue: Gradle Daemon Locks
**Solution**: Run `./gradlew --stop` before builds

### Issue: Permission Denied on Scripts
**Solution**: Run `chmod +x *.sh`

### Issue: Out of Memory during Build
**Solution**: Add `org.gradle.jvmargs=-Xmx2048m` to `gradle.properties`

---

## Agent Communication Protocol

When updating this file or creating new agent documentation:
1. Use markdown format exclusively
2. Keep sections clearly separated
3. Update TODO lists when completing work
4. Log significant decisions in DECISIONS.md
5. Record progress in PROGRESS.md

---

## External Resources

- [Android Developer Documentation](https://developer.android.com/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Termux Wiki](https://wiki.termux.com/)
- [OpenCode CLI](https://github.com/opencode-ai/opencode)

---

**Last Updated**: 2026-03-02  
**Version**: 1.0.0  
**Status**: Active Development
