plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.spelltester"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.spelltester"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
    }
}
dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.chromium.net:cronet-embedded:119.6045.31")
    // Room and Architectural Components
    val roomVersion="2.4.2"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.room:room-ktx:2.2.1")
    kapt ("androidx.room:room-compiler:$roomVersion")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.0")

    //stack bar
    implementation ("com.github.nisrulz:stackedhorizontalprogressbar:1.0.3")

    // for adding recyclerview
    implementation("androidx.recyclerview:recyclerview:1.2.0")
    //Gson
    implementation("com.google.code.gson:gson:2.8.8")
    // for adding cardview
    implementation("androidx.cardview:cardview:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.38.1")
    kapt("com.google.dagger:hilt-compiler:2.38.1")

    // firebase
    implementation("com.google.firebase:firebase-analytics")
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.2")
    implementation("com.google.firebase:firebase-sessions:1.2.4")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
}