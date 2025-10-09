plugins {
    id("speechmate.android.library")
    id("speechmate.android.hilt")
}

android {
    namespace = "com.speech.datastore"

    buildTypes {
        release { consumerProguardFiles("consumer-rules.pro") }
    }
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.androidx.datastore)
    implementation(libs.gson)
}
