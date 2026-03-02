# Architecture Decision Records (ADR)

## ADR-001: Project Structure - Standard Android Gradle Project

**Status**: Accepted  
**Date**: 2026-03-02

### Context
Need to create a maintainable Android project that can be built on Linux x86-64 and optionally on Android via Termux.

### Decision
Use standard Android Gradle project structure with Kotlin DSL for build scripts.

### Consequences
- **Positive**: Industry standard, well-documented, IDE support
- **Negative**: Requires JDK and Android SDK setup

---

## ADR-002: UI Framework - Jetpack Compose

**Status**: Accepted  
**Date**: 2026-03-02

### Context
Need modern, declarative UI that's easy to maintain and modify by LLM agents.

### Decision
Use Jetpack Compose exclusively for UI development.

### Rationale
- Declarative syntax is easier for LLMs to generate correctly
- Less boilerplate than traditional Android Views
- Modern Android standard

---

## ADR-003: Storage - External Shared Directory

**Status**: Accepted  
**Date**: 2026-03-02

### Context
App needs to share config files with external CLI tools (OpenCode/Kilo running in Termux).

### Decision
Use `Documents/KiloWorkspace` as shared storage with proper Android 11+ scoped storage permissions.

### Implementation
- `MANAGE_EXTERNAL_STORAGE` permission for broad access
- `DocumentsContract` for file operations
- Fallback to app-private storage if permissions denied

---

## ADR-004: Authentication - Intent Filter Interception

**Status**: Accepted  
**Date**: 2026-03-02

### Context
OAuth flows in CLI tools use localhost callbacks that don't work well on mobile.

### Decision
Intercept custom URI schemes (`opencode://auth`) and localhost redirects via `intent-filter`.

### Implementation
- `AuthHandlerActivity` handles all auth redirects
- Writes tokens to shared `auth.json`
- Shows Toast confirmation to user

---

## ADR-005: Build Scripts - Shell Scripts with Gradle Wrapper

**Status**: Accepted  
**Date**: 2026-03-02

### Context
Need reproducible builds on multiple platforms (Linux x86-64, Termux arm64).

### Decision
Use shell scripts wrapping Gradle wrapper with platform detection.

### Scripts
1. `build-android.sh` - Standard Linux builds
2. `termux-build.sh` - Termux-specific with package adjustments
3. `install-prerequisites.sh` - Environment setup

---

## ADR-006: Documentation - Markdown-First Approach

**Status**: Accepted  
**Date**: 2026-03-02

### Context
End user has minimal technical experience. Need clear, comprehensive documentation.

### Decision
Write all documentation in Markdown with:
- Step-by-step instructions
- Copy-paste code blocks
- Troubleshooting sections
- Visual hierarchy

### Files
- `README.md` - Project overview
- `BUILD.md` - Build instructions
- `USAGE.md` - User guide
- `TERMUX.md` - Mobile development
- `AGENTS.md` - LLM coordination
