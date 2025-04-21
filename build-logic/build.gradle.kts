plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "speechmate.android.hilt"
            implementationClass = "com.example.app.HiltAndroidPlugin"
        }
        register("kotlinHilt") {
            id = "speechmate.kotlin.hilt"
            implementationClass = "com.example.app.HiltKotlinPlugin"
        }
    }
}