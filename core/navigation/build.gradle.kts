plugins {
    id("speechmate.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.speech.navigation"

    buildTypes {
        release { consumerProguardFiles("consumer-rules.pro") }
    }
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.androidx.navigation.ui)
    implementation(libs.kotlinx.serialization.json)
}
