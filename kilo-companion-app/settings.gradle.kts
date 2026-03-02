// =============================================================================
// Settings Gradle Configuration
// =============================================================================
// This file defines the project structure and plugin management.
// It configures where to look for plugins and project naming.
// =============================================================================

pluginManagement {
    repositories {
        google()          // Android-specific plugins
        mavenCentral()    // General Java/Kotlin plugins
        gradlePluginPortal()  // Gradle community plugins
    }
}

dependencyResolutionManagement {
    // Use repositories mode to enforce consistent repository sources
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Define the root project name
rootProject.name = "KiloCompanion"

// Include the app module
include(":app")
