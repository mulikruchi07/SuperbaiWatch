// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// In dependencies block of your wear/build.gradle.kts

// Compose for Wear OS
implementation("androidx.compose.ui:ui")
implementation("androidx.wear.compose:compose-material:1.3.1") // Check for latest stable version
implementation("androidx.wear.compose:compose-foundation:1.3.1")

// Compose Navigation for handling screens and swipe-to-dismiss gesture
implementation("androidx.wear.compose:compose-navigation:1.3.1")

// Google Play Services for Wearable Data Layer API
// This is CRITICAL for communicating with the phone
implementation("com.google.android.gms:play-services-wearable:18.1.0") // Check for latest stable version