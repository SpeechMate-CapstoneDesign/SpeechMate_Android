plugins {
    id("speechmate.android.library")
    id("speechmate.android.compose")
}


android {
    namespace = "com.speech.common_ui"
}

dependencies {
    implementation(libs.orbit.core)

    implementation(projects.core.designsystem)
}


