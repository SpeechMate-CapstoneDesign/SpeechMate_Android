plugins {
    id("speechmate.android.feature")
}

android {
    namespace = "com.speech.practice"
}

dependencies {
    implementation(libs.audio.waveform)
}

