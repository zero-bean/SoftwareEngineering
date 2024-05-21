plugins {
    alias(libs.plugins.androidApplication)
    // 구글 서비스 사용을 위한 플러그인 추가
    id("com.google.gms.google-services")
}

android {
    namespace = "com.gcu.anniversary"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gcu.anniversary"
        minSdk = 26
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-database")

    // circular imageView
    implementation ("com.mikhaellopez:circularimageview:4.3.1")

    // Glide - 이미지 url 가져오기
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    // Material Design 3
    implementation ("com.google.android.material:material:1.13.0-alpha01")

    // Lombok
    implementation ("org.projectlombok:lombok:1.18.32")
    annotationProcessor ("org.projectlombok:lombok:1.18.32")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.firebase:firebase-messaging:24.0.0")
}