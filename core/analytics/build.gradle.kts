import java.io.FileInputStream
import java.util.Properties

plugins {
    id("speechmate.android.library")
    id("speechmate.android.hilt")
}

android {
    namespace = "com.speech.analytics"

    defaultConfig {
        val properties = Properties().apply {
            load(FileInputStream(rootProject.file("local.properties")))
        }

        buildConfigField(
            "String",
            "AMPLITUDE_API_KEY",
            properties["AMPLITUDE_API_KEY"] as String,
        )
    }

    buildFeatures {
        buildConfig = true
    }
}


dependencies {
    implementation(libs.amplitude.analytics)
}
