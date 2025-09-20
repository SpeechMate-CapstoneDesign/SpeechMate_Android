plugins {
    id("speechmate.android.library")
    id("speechmate.android.compose")
}

android {
    namespace = "com.speech.designsystem"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(projects.core.commonUi)
}


