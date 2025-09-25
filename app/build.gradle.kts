plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("com.google.firebase.crashlytics")
}

android {
    namespace = "com.ahofama.nextclass"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ahofama.nextclass"
        minSdk = 28
        targetSdk = 34
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

    buildFeatures{
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.drawerlayout)

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))

    //! Firebase dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-auth:22.3.1")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Material Design Components - Updated to latest version
    implementation("com.google.android.material:material:1.12.0")

    // ConstraintLayout & CardView
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")

    // Material Design Components
    implementation("com.google.android.material:material:1.10.0")
// CircleImageView for profile pictures
    implementation("de.hdodenhof:circleimageview:3.1.0")
// Lottie for animations (optional but recommended)
    implementation("com.airbnb.android:lottie:6.1.0")
// RecyclerView (usually included with material)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
// ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
// CardView
    implementation("androidx.cardview:cardview:1.0.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}