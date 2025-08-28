plugins {
    id("speechmate.android.feature")
}

android {
    namespace = "com.speech.splash"
}

dependencies {
    implementation(projects.core.designsystem)
}