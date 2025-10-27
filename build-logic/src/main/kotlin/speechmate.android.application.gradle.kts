import com.speech.build.logic.configureHiltAndroid
import com.speech.build.logic.configureKotestAndroid
import com.speech.build.logic.configureKotlinAndroid

plugins {
    id("com.android.application")
}

configureKotlinAndroid()
configureHiltAndroid()
configureKotestAndroid()
