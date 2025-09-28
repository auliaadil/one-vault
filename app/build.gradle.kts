import org.gradle.kotlin.dsl.release

plugins {
    id("app.cash.sqldelight")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.adilstudio.project.onevault"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.adilstudio.project.onevault"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Configure native libraries for ONNX Runtime
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(findProperty("ONEVAULT_STORE_FILE") ?: "")
            storePassword = findProperty("ONEVAULT_STORE_PASSWORD") as String? ?: ""
            keyAlias = findProperty("ONEVAULT_KEY_ALIAS") as String? ?: ""
            keyPassword = findProperty("ONEVAULT_KEY_PASSWORD") as String? ?: ""
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
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
        buildConfig = true
        dataBinding = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Jetpack Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.icons.extended)

    // Koin (Dependency Injection)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Coroutines
    implementation(libs.coroutines.android)

    // SQLDelight (Local Database)
    implementation(libs.sqldelight.android.driver)
    implementation(libs.sqldelight.coroutines.extensions)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Google ML Kit (OCR)
    implementation(libs.google.mlkit.text.recognition)

    // Image Loading
    implementation(libs.coil.compose)

    // Biometric Authentication
    implementation(libs.androidx.biometric)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Compose Reorderable for drag and drop
    implementation(libs.reorderable)

    // DataStore (for settings)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.junit.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
