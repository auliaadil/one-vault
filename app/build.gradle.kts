plugins {
    id("app.cash.sqldelight")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

// Git versioning functions using providers for configuration cache compatibility
fun getGitTag(): Provider<String> = providers.exec {
    commandLine("git", "describe", "--tags", "--abbrev=0")
    isIgnoreExitValue = true
}.standardOutput.asText.map { output ->
    val result = output.trim()
    result.ifEmpty { "v0.0.0" }
}.orElse("v0.0.0")

fun getGitCommitCount(): Provider<Int> = providers.exec {
    commandLine("git", "rev-list", "--count", "HEAD")
    isIgnoreExitValue = true
}.standardOutput.asText.map { output ->
    val result = output.trim()
    if (result.isNotEmpty()) result.toIntOrNull() ?: 1 else 1
}.orElse(1)

fun getGitHash(): Provider<String> = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
    isIgnoreExitValue = true
}.standardOutput.asText.map { output ->
    val result = output.trim()
    result.ifEmpty { "unknown" }
}.orElse("unknown")

// Generate version name and code using providers
val appVersionName: Provider<String> = getGitTag().map { tag ->
    // Remove 'v' prefix if present
    val cleanTag = if (tag.startsWith("v")) tag.substring(1) else tag

    // Check if this is a release build by looking at gradle.startParameter.taskNames
    val isReleaseBuild = gradle.startParameter.taskNames.any { taskName ->
        taskName.contains("Release", ignoreCase = true) ||
        taskName.contains("release", ignoreCase = false)
    }

    if (isReleaseBuild) {
        cleanTag
    } else {
        "$cleanTag-debug"
    }
}

val appVersionCode: Provider<Int> = getGitCommitCount().map { commitCount ->
    maxOf(commitCount, 1)
}

android {
    namespace = "com.adilstudio.project.onevault"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.adilstudio.project.onevault"
        minSdk = 26
        targetSdk = 36
        versionCode = appVersionCode.get()
        versionName = appVersionName.get()

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
    implementation(platform(libs.firebase.bom))

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

    // Firebase
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

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

// Custom task to display version information
tasks.register("printVersionInfo") {
    doLast {
        println("=== OneVault Version Information ===")
        println("Version Name: ${appVersionName.get()}")
        println("Version Code: ${appVersionCode.get()}")
        println("Git Tag: ${getGitTag().get()}")
        println("Git Hash: ${getGitHash().get()}")
        println("Git Commit Count: ${getGitCommitCount().get()}")
        println("=====================================")
    }
}
