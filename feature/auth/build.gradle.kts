plugins {
    id("speechmate.android.feature")
}

android {
    namespace = "com.speech.auth"
}

dependencies {
    implementation(libs.kakao.user)
}

