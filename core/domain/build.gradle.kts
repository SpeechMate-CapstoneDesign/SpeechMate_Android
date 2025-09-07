plugins {
    id("speechmate.kotlin.library")
    id("speechmate.kotlin.hilt")
}


dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.androidx.paging.common)
}
