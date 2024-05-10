buildscript {
    dependencies {
        // 구글 서비스 이용을 위한 의존성 추가
        classpath(libs.google.services)
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
}