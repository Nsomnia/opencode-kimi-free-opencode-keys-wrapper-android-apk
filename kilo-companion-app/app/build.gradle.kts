// =============================================================================
// App Module Build Configuration
// =============================================================================
// This file defines how the Android app is built, including:
// - Android SDK versions and features
// - Dependencies (libraries the app uses)
// - Build types (debug vs release)
// - Compilation options
// - NDK/Native code support for Node.js integration
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
    // Namespace is required for Android Gradle Plugin 8.0+
    namespace = "com.kilo.companion"
    
    // -----------------------------------------------------------------------------
    // SDK Configuration
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
        
        // NDK configuration for Node.js integration
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
                arguments += "-DANDROID_STL=c++_shared"
            }
        }

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
        }
    }
    
    // -----------------------------------------------------------------------------
    // Build Types
    // -----------------------------------------------------------------------------
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    
    // -----------------------------------------------------------------------------
    // Native Build Configuration
    // -----------------------------------------------------------------------------
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
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
        buildConfig = true
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
        jniLibs {
            pickFirsts += listOf("**/libnodejs-mobile.so")
        }
    }
}

// =============================================================================
// Dependencies
// =============================================================================

dependencies {
    // AndroidX Core Libraries
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Jetpack Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    // Compose UI libraries
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")
    
    // Storage
    implementation("androidx.documentfile:documentfile:1.0.1")
    
    // JSON Processing
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("com.google.code.gson:gson:2.10.1")
    
    // WebView
    implementation("com.google.accompanist:accompanist-webview:0.32.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
