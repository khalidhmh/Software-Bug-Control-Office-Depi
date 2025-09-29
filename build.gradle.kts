// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // الـplugins اللى معمولة alias فى gradle/libs.versions.toml
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// 👈 هنا بنضيف الـrepositories والـclasspath الخاص بـ Hilt plugin
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
