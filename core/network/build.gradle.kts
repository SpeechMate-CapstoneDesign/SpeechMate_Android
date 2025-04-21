import java.io.FileInputStream
import java.util.Properties

plugins {
    id("speechmate.android.library")
    id("speechmate.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.speech.network"

//    defaultConfig {
//        val properties = Properties().apply {
//            load(FileInputStream(rootProject.file("local.properties")))
//        }
//
//        buildConfigField(
//            "String",
//            "SPEECHMATE_BASE_URL",
//            properties["SPEECHMATE_BASE_URL"] as String
//        )
//    }
//
//    buildFeatures {
//        buildConfig = true
//    }
}

dependencies {
    implementation(projects.core.domain)


    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.okhttp.logging)
}