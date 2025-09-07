plugins {
    id("speechmate.android.feature")
}

android {
    namespace = "com.speech.mypage"
}

dependencies {
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}

