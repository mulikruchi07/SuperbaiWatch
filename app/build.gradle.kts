plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.superbai.wear"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.superbai.wear"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // Core
    implementation ("androidx.core:core-ktx:1.13.1")
    // Wear OS Compose
    implementation ("androidx.wear.compose:compose-foundation:1.3.1")
    // Splash screen
    implementation ("androidx.core:core-splashscreen:1.0.1")
    // Lifecycle
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    // Debug
    debugImplementation ("androidx.compose.ui:ui-tooling")
    implementation("androidx.wear.compose:compose-material:1.3.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.wear.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}