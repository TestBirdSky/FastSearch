plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

setupApp()

android.defaultConfig.applicationId = "com.fdemo.xxx"

dependencies {
    implementation(project(mapOf("path" to ":core")))

    //android
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.4.1")
    kapt("androidx.lifecycle:lifecycle-compiler:2.4.1")
    kapt("androidx.room:room-compiler:2.2.4")

    //google play
    implementation(platform("com.google.firebase:firebase-bom:29.3.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.facebook.android:facebook-android-sdk:12.3.0")
    implementation("com.google.android.gms:play-services-ads:20.6.0")
    implementation ("com.android.installreferrer:installreferrer:2.2")

    //other lib
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.blankj:utilcodex:1.31.0")
    implementation("com.airbnb.android:lottie:5.2.0")
    implementation("com.tencent:mmkv:1.2.13")
    implementation ("com.github.li-xiaojun:XPopup:2.9.0")
    implementation ("com.github.liangjingkanji:BRV:1.3.82")
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")
}
