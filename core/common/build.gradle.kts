plugins {
    id("speechmate.android.library")
    id("speechmate.android.compose")
}


android {
    namespace = "com.speech.common"
}

dependencies {
    implementation(projects.core.designsystem)
}

