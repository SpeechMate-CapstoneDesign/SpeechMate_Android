import java.io.FileInputStream
import java.util.Properties

plugins {
    id("speechmate.android.application")
    //  alias(libs.plugins.firebase.crashlytics)
    // alias(libs.plugins.google.services)
}



android {
    namespace = "com.speech.speechmate"

    defaultConfig {
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    defaultConfig {
        val properties = Properties().apply {
            load(FileInputStream(rootProject.file("local.properties")))
        }

        buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
            properties["KAKAO_NATIVE_APP_KEY"] as String
        )

        manifestPlaceholders["KAKAO_REDIRECT_URI"] = properties["KAKAO_REDIRECT_URI"] as String
    }


    buildFeatures {
        buildConfig = true
    }

}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.domain)

    implementation(projects.feature.main)

    implementation(libs.kakao.user)

}