import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.playtronic"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.playtronic"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }


}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    implementation("com.google.firebase:firebase-analytics")
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries


    implementation("com.google.firebase:firebase-auth")


    implementation ("com.google.android.gms:play-services-auth:21.0.0")

    implementation ("com.facebook.android:facebook-android-sdk:12.2.0")

    implementation ("androidx.browser:browser:1.8.0")

    implementation ("org.twitter4j:twitter4j-core:4.0.7")

    implementation("com.google.firebase:firebase-firestore")

    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.google.android.material:material:1.11.0")


    implementation ("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")

    implementation ("androidx.cardview:cardview:1.0.0")




}