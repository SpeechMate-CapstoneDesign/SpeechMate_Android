import java.io.FileInputStream
import java.util.Properties

plugins {
    id("speechmate.android.application")
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.android.application)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.speech.speechmate"

    defaultConfig {
        targetSdk = 35
        versionCode = 5
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    defaultConfig {
        val properties = Properties().apply {
            load(FileInputStream(rootProject.file("local.properties")))
        }

        buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
            "\"${properties.getProperty("KAKAO_NATIVE_APP_KEY")}\"",
        )

        manifestPlaceholders["KAKAO_REDIRECT_URI"] = properties.getProperty("KAKAO_REDIRECT_URI")
    }

    signingConfigs {
        val keystoreProperties = Properties()
        keystoreProperties.load(rootProject.file("keystore.properties").bufferedReader())

        create("release") {
            storeFile = rootProject.file(keystoreProperties.getProperty("STORE_PATH"))
            storePassword = keystoreProperties.getProperty("STORE_PASSWORD")
            keyAlias = keystoreProperties.getProperty("KEY_ALIAS")
            keyPassword = keystoreProperties.getProperty("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            manifestPlaceholders["APP_NAME"] = "@string/app_name"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            manifestPlaceholders["APP_NAME"] = "@string/app_name_debug"
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

baselineProfile {
    dexLayoutOptimization = true
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.feature.main)

    implementation(libs.kakao.user)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.profileinstaller)
//    "baselineProfile"(project(":baselineprofile"))
}
