import java.io.FileInputStream
import java.util.Properties

plugins {
    id("speechmate.android.library")
    id("speechmate.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.speech.network"

    defaultConfig {
        val localProperties = Properties().apply {
            load(rootProject.file("local.properties").bufferedReader())
        }

        buildConfigField(
            "String",
            "SPEECHMATE_BASE_URL",
            localProperties["SPEECHMATE_BASE_URL"] as String,
        )
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release { consumerProguardFiles("consumer-rules.pro") }
    }
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.okhttp.logging)
}
