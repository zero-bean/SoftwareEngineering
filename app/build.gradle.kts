plugins {
    alias(libs.plugins.androidApplication)
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

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
            res.srcDirs("src/main/res")
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
        getByName("test") {
            java.srcDirs("src/test/java")
            res.srcDirs("src/test/res")
            manifest.srcFile("src/test/AndroidManifest.xml")
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

    // Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-messaging:24.0.0")

    // Circular ImageView
    implementation("com.mikhaellopez:circularimageview:4.3.1")

    // Glide - 이미지 url 가져오기
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Material Design 3
    implementation("com.google.android.material:material:1.13.0-alpha01")

    // Lombok
    implementation("org.projectlombok:lombok:1.18.32")
    implementation(libs.espresso.intents)
    implementation(libs.fragment.testing)
    testImplementation(project(":app"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    // AndroidX libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // AndroidX Test libraries
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    // Mockito for mocking
    testImplementation("org.mockito:mockito-core:4.2.0")
    androidTestImplementation("org.mockito:mockito-android:4.2.0")
    androidTestImplementation("org.mockito:mockito-inline:4.2.0")

    // Robolectric for unit testing
    testImplementation("org.robolectric:robolectric:4.7.3")
}
