// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    apply(from = "repositories.gradle.kts")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        val kotlinVersion = rootProject.extra["kotlinVersion"].toString()
        classpath(rootProject.extra["androidPlugin"].toString())
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath("org.mozilla.rust-android-gradle:plugin:0.9.0")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
    }
}

allprojects {
    apply(from = "${rootProject.projectDir}/repositories.gradle.kts")
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

// skip uploading the mapping to Crashlytics
subprojects {
    tasks.whenTaskAdded {
        if (name.contains("uploadCrashlyticsMappingFile")) enabled = false
    }
}