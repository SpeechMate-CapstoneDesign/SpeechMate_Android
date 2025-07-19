plugins {
    id("speechmate.android.library")
    id("speechmate.android.hilt")
}

android {
    namespace = "com.speech.datastore"
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.androidx.datastore)
    implementation(libs.gson)
}