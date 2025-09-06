plugins {
    id("speechmate.android.feature")
}

android {
    namespace = "com.speech.practice"
}

dependencies {
    implementation(libs.accompanist.permission)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui.compose)
    implementation(libs.media3.common)
    implementation(libs.media3.transformer)

    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.video)
    implementation(libs.camerax.view)
    implementation(libs.camerax.mlkit.vision)
    implementation(libs.camerax.extensions)
}

