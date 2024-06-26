// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.1" apply false
}

buildscript {
    repositories {
        // Other repositories...
        maven { url = uri("https://jitpack.io") }
        google()
        mavenCentral() // Add this line
    }
    dependencies {
        // Your dependencies...
    }
}
