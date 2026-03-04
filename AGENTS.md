# CodePal - Architecture Guide

## Vision
Build the first truly mobile-native AI coding environment. Not a Termux wrapper, not a remote client - a complete, self-contained development tool that happens to run on Android.

## Core Principles

### 1. Self-Contained
- Embedded Node.js runtime (nodejs-mobile)
- Bundled opencode & kilo packages
- Local execution, no external dependencies
- Works offline entirely

### 2. Mobile-Native UX
- Touch-optimized interface (not desktop ported)
- Gesture-based interactions
- Predictive UI with Material 3
- Optimized for small screens

### 3. Professional Quality
- Zero "slop" - every feature polished
- Comprehensive error handling
- Proper logging and debugging
- Performance optimized

## Architecture

### Node.js Integration
```
app/src/main/
├── cpp/                          # JNI bridge for nodejs-mobile
│   └── native-node.cpp
├── assets/
│   ├── nodejs/                   # Node.js runtime binaries
│   ├── opencode/                 # opencode npm package
│   └── kilo/                     # kilo npm package
└── java/com/kilo/companion/
    ├── nodejs/                   # Node.js service management
    │   ├── NodeService.kt
    │   └── NodeEventListener.kt
    └── runtime/                  # Runtime management
```

### Key Components

#### 1. NodeService (Foreground Service)
- Manages Node.js runtime lifecycle
- Handles process spawning
- Monitors memory/CPU usage
- Auto-restart on crash

#### 2. RuntimeManager
- Initializes Node.js environment
- Sets up proper paths (HOME, NODE_PATH)
- Manages npm package installation
- Handles version updates

#### 3. UIManager (Material 3)
- Bottom navigation with 5 tabs:
  - **Editor**: Code editor with syntax highlighting
  - **Terminal**: Interactive terminal
  - **Files**: Project file manager
  - **AI**: Chat interface
  - **Settings**: Configuration

#### 4. StorageManager
- Handles Android 11+ scoped storage
- Manages workspace directory
- Git operations
- File sync

## Technical Stack

### Native Layer
- **nodejs-mobile**: v18.20.4 (latest stable)
- **JNI**: Bridge between Kotlin and Node.js
- **V8**: JavaScript engine (bundled with Node)

### Android Layer
- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM with ViewModel
- **Storage**: Scoped storage with FileProvider

### Bundled Packages
- **opencode**: Latest from npm
- **kilo**: Latest from npm
- **node_modules**: Pre-bundled to avoid installation on device

## Development Workflow

### Adding Node.js Native Module
1. Build nodejs-mobile AAR
2. Add to app/build.gradle dependencies
3. Create JNI bridge
4. Test on real device (emulator unreliable for Node)

### Updating npm Packages
1. Update in assets directory
2. Version bump in build.gradle
3. Test thoroughly (packages may have native deps)

### UI Development
1. Use Compose Preview
2. Test on multiple screen sizes
3. Ensure touch targets >= 48dp
4. Support dark/light themes

## File Structure

```
CodePal/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── assets/
│   │   │   ├── nodejs/
│   │   │   ├── opencode/
│   │   │   └── kilo/
│   │   ├── cpp/
│   │   ├── java/com/kilo/companion/
│   │   └── res/
│   └── build.gradle.kts
├── nodejs-mobile/              # Git submodule or prebuilt
├── docs/
└── AGENTS.md
```

## Critical Notes

### Android 11+ (API 30+)
- Use MANAGE_EXTERNAL_STORAGE permission
- Or Scoped Storage with DocumentFile
- FileProvider for sharing files

### Node.js on Android
- HOME directory: /data/data/com.kilo.companion/files
- NODE_PATH must be set properly
- Native modules need to be compiled for Android arch

### Performance
- Node.js uses significant memory (~100MB+)
- Monitor with ActivityManager
- Kill gracefully to avoid zombies

## Git Workflow
- Commit frequently
- Use feature branches for major changes
- Test on device before pushing
- Keep main branch stable

## Success Criteria
✓ Launch without Termux
✓ Run opencode/kilo commands
✓ Edit files with syntax highlighting
✓ Smooth 60fps UI
✓ Works offline completely
✓ Professional polish (no "slop")
