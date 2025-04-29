plugins {
    id("speechmate.android.feature")
}

android {
    namespace = "com.speech.main"

}

dependencies {
//    implementation(projects.feature.auth)
   implementation(projects.feature.home)

    implementation(libs.kakao.user)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
}