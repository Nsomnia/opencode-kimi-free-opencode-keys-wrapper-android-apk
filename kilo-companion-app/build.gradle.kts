// =============================================================================
// Root Project Build Configuration
// =============================================================================
// This file configures the overall project build settings.
// It defines plugin versions that apply to all modules.
// =============================================================================

plugins {
    // Android Gradle Plugin - manages Android app compilation
    id("com.android.application") version "8.2.2" apply false
    
    // Kotlin Android Plugin - enables Kotlin support for Android
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    
    // Kotlin Serialization - for JSON handling
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
}

// Define versions for all modules to use
// Centralizing versions ensures consistency across the project
allprojects {
    ext {
        set("compileSdk", 34)
        set("targetSdk", 34)
        set("minSdk", 26)  // Android 8.0 - good balance of features and compatibility
        set("composeBom", "2024.02.00")
    }
}

// Clean task - removes all build artifacts
tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
