# Development Workflow Guide

## Quick Start for New Agents

### Step 1: Environment Setup
```bash
# 1. Install prerequisites
./install-prerequisites.sh

# 2. Start auto-git for version control
./auto-git.sh &

# 3. Verify Android SDK is available
echo $ANDROID_HOME
```

### Step 2: Understanding the Codebase

#### Key Files for Each Feature

| Feature | Key Files |
|---------|-----------|
| Config Management | `ConfigManagerScreen.kt`, `SharedStorageManager.kt` |
| Authentication | `AuthHandlerActivity.kt`, `AndroidManifest.xml` |
| Web Wrapper | `WebViewScreen.kt` |
| Navigation | `MainActivity.kt`, `Navigation.kt` |

### Step 3: Making Changes

1. **Read existing code** - Understand current implementation
2. **Update TODO list** - Mark current task as "in progress"
3. **Implement** - Follow code style and add comments
4. **Test** - Run `./build-android.sh debug`
5. **Document** - Update relevant .md files
6. **Commit** - Auto-git handles this, but verify with `git log`

### Step 4: Code Review Checklist

Before considering work complete:
- [ ] Kotlin code follows [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [ ] All strings extracted to `strings.xml`
- [ ] Colors defined in `Color.kt` theme
- [ ] Permissions declared in `AndroidManifest.xml`
- [ ] Error handling with try/catch
- [ ] User feedback (Toasts/Snackbars) for actions
- [ ] Comments explain "why" not "what"

---

## Build Process

### Debug Build (Development)
```bash
./build-android.sh debug
```
Output: `kilo-companion-app/app/build/outputs/apk/debug/app-debug.apk`

### Release Build (Distribution)
```bash
./build-android.sh release
```
Output: `kilo-companion-app/app/build/outputs/apk/release/app-release-unsigned.apk`

### Installing to Device
```bash
# Via ADB
adb install -r kilo-companion-app/app/build/outputs/apk/debug/app-debug.apk

# Or copy to device and install manually
```

---

## Testing Strategy

### Manual Testing Checklist

1. **Fresh Install Test**
   - Uninstall existing app
   - Install fresh APK
   - Grant permissions
   - Verify first-run experience

2. **Config Management Test**
   - Create `Documents/KiloWorkspace/.config/kilo/opencode.json`
   - Open in app
   - Edit and save
   - Verify changes on filesystem

3. **Auth Flow Test**
   - Trigger OAuth from CLI
   - Verify app intercepts redirect
   - Check `auth.json` is created
   - Verify token format

4. **WebView Test**
   - Start local server on `127.0.0.1:3000`
   - Enter URL in app
   - Verify page loads
   - Test interaction

### Automated Testing
```bash
./gradlew test          # Unit tests
./gradlew connectedTest # Instrumented tests (needs device)
```

---

## Git Workflow

### Auto-Git Script Behavior
- Runs continuously in background
- Commits every 60 seconds if changes exist
- Commit message: `Auto-commit: YYYY-MM-DD HH:MM:SS UTC`
- No push - local commits only

### Manual Git Operations
```bash
# Check status
git status

# View recent commits
git log --oneline -10

# Reset to last good commit (emergency)
git reset --hard HEAD~1
```

---

## Debugging

### View Logs
```bash
# All app logs
adb logcat -s "KiloCompanion:*"

# Filter for errors only
adb logcat -s "KiloCompanion:E"
```

### Common Debug Scenarios

**App crashes on startup**:
- Check `AndroidManifest.xml` for missing activities
- Verify all permissions declared
- Check for missing imports in Kotlin files

**File operations fail**:
- Verify `MANAGE_EXTERNAL_STORAGE` permission granted
- Check `Documents/KiloWorkspace` exists
- Review logcat for SecurityException

**Auth not intercepting**:
- Verify intent-filter in manifest
- Check URL scheme matches exactly
- Ensure `android:exported="true"`

---

## Performance Guidelines

1. **File I/O**: Use coroutines (`lifecycleScope`) for disk operations
2. **WebView**: Enable caching, limit JavaScript if not needed
3. **Memory**: Use `remember` and derived state in Compose
4. **Battery**: Avoid continuous polling, use event-driven updates
