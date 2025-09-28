# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Keep crash reporting attributes
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.adilstudio.project.onevault.**$$serializer { *; }
-keepclassmembers class com.adilstudio.project.onevault.** {
    *** Companion;
}
-keepclasseswithmembers class com.adilstudio.project.onevault.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# SQLDelight
-keep class com.adilstudio.project.onevault.sqldelight.** { *; }
-keep class app.cash.sqldelight.** { *; }

# Google ML Kit
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_text_common.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Koin
-keep class org.koin.** { *; }
-keep class kotlin.reflect.jvm.internal.** { *; }

# Biometric
-keep class androidx.biometric.** { *; }

# DataStore
-keep class androidx.datastore.** { *; }

# Coil
-keep class coil.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-keep class kotlin.coroutines.jvm.internal.** { *; }

# Keep your app's data classes and models
-keep class com.adilstudio.project.onevault.data.** { *; }
-keep class com.adilstudio.project.onevault.model.** { *; }
