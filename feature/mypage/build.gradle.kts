import java.util.Properties
import kotlin.apply

plugins {
    id("speechmate.android.feature")
}

android {
    namespace = "com.speech.mypage"

    defaultConfig {
        val properties = Properties().apply {
            load(rootProject.file("local.properties").bufferedReader())
        }

        buildConfigField(
            "String",
            "SPEECHMATE_INQUIRY_URL",
            properties["SPEECHMATE_INQUIRY_URL"] as String
        )
        buildConfigField(
            "String",
            "SPEECHMATE_PRIVACY_POLICY_URL",
            properties["SPEECHMATE_PRIVACY_POLICY_URL"] as String
        )
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}

