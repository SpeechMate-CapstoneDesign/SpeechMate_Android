plugins {
    id("speechmate.android.library")
    id("speechmate.android.compose")
}


android {
    namespace = "com.speech.common_ui"
}

dependencies {
    implementation(libs.androidx.paging.compose)
}



