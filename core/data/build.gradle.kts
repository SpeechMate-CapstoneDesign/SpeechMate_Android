plugins {
    id("speechmate.android.library")
    id("speechmate.android.hilt")
}

android {
    namespace = "com.speech.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.datastore)

    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.paging.runtime)

}
