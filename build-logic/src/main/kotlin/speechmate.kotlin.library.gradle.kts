import com.speech.build.logic.configureKotest
import com.speech.build.logic.configureKotlin
import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
}

configureKotlin()
configureKotest()
