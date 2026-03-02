// =============================================================================
// App Module Build Configuration
// =============================================================================
// This file defines how the Android app is built, including:
// - Android SDK versions and features
// - Dependencies (libraries the app uses)
// - Build types (debug vs release)
// - Compilation options
// =============================================================================

plugins {
    // Android application plugin - turns this module into an APK
    id("com.android.application")
    
    // Kotlin Android plugin - enables Kotlin language features
    id("org.jetbrains.kotlin.android")
    
    // Kotlin serialization - for JSON parsing
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    // -----------------------------------------------------------------------------
    // SDK Configuration
    // -----------------------------------------------------------------------------
    // compileSdk: The version of Android SDK used to compile the app
    // targetSdk: The highest Android version the app is tested against
    // minSdk: The lowest Android version the app supports
    // -----------------------------------------------------------------------------
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.kilo.companion"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        
        // Test runner for instrumentation tests
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Vector drawable support for older Android versions
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    
    // -----------------------------------------------------------------------------
    // Build Types
    // -----------------------------------------------------------------------------
    // Debug: Development builds with debugging enabled
    // Release: Production builds with optimizations
    // -----------------------------------------------------------------------------
    buildTypes {
        release {
            // Enable code shrinking and obfuscation
            isMinifyEnabled = true
            isShrinkResources = true
            
            // ProGuard rules for code shrinking
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        
        debug {
            // Debug builds don't shrink code for faster builds
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    
    // -----------------------------------------------------------------------------
    // Compilation Options
    // -----------------------------------------------------------------------------
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    // -----------------------------------------------------------------------------
    // Jetpack Compose Configuration
    // -----------------------------------------------------------------------------
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    
    // -----------------------------------------------------------------------------
    // Packaging Options
    // -----------------------------------------------------------------------------
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// =============================================================================
// Dependencies
// =============================================================================
// These are external libraries that our app uses.
// Organized by category for clarity.
// =============================================================================

dependencies {
    // -----------------------------------------------------------------------------
    // AndroidX Core Libraries
    // -----------------------------------------------------------------------------
    // These provide fundamental Android functionality
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // -----------------------------------------------------------------------------
    // Jetpack Compose BOM (Bill of Materials)
    // -----------------------------------------------------------------------------
    // Using BOM ensures all Compose libraries are compatible versions
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    // Compose UI libraries
    implementation("androidx.compose.ui:ui")                    // Core UI components
    implementation("androidx.compose.ui:ui-graphics")           // Graphics primitives
    implementation("androidx.compose.ui:ui-tooling-preview")    // Preview support
    implementation("androidx.compose.material3:material3")      // Material Design 3
    implementation("androidx.compose.material:material-icons-extended") // Extended icons
    
    // Navigation for Compose - enables screen-to-screen navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // ViewModel for Compose - lifecycle-aware data management
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // -----------------------------------------------------------------------------
    // Storage & File System
    // -----------------------------------------------------------------------------
    // DocumentFile for SAF (Storage Access Framework) operations
    implementation("androidx.documentfile:documentfile:1.0.1")
    
    // -----------------------------------------------------------------------------
    // JSON Processing
    // -----------------------------------------------------------------------------
    // Kotlinx Serialization for JSON parsing
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    
    // Gson as a fallback JSON parser (widely used, good for edge cases)
    implementation("com.google.code.gson:gson:2.10.1")
    
    // -----------------------------------------------------------------------------
    // WebView Enhancements
    // -----------------------------------------------------------------------------
    // Accompanist WebView - better WebView integration with Compose
    implementation("com.google.accompanist:accompanist-webview:0.32.0")
    
    // -----------------------------------------------------------------------------
    // Coroutines
    // -----------------------------------------------------------------------------
    // Kotlin Coroutines for async operations (file I/O, network)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // -----------------------------------------------------------------------------
    // Testing
    // -----------------------------------------------------------------------------
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    // -----------------------------------------------------------------------------
    // Debug Tools
    // -----------------------------------------------------------------------------
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
