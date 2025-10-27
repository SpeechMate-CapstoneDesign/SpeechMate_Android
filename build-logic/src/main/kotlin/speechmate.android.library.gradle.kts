import com.speech.build.logic.configureCoroutineAndroid
import com.speech.build.logic.configureHiltAndroid
import com.speech.build.logic.configureKotest
import com.speech.build.logic.configureKotlinAndroid


plugins {
    id("com.android.library")
}

configureKotlinAndroid()
configureHiltAndroid()
configureKotest()
configureCoroutineAndroid()
