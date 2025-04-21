plugins {
    id("speechmate.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.speech.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.ui)
    implementation(libs.kotlinx.serialization.json)
}