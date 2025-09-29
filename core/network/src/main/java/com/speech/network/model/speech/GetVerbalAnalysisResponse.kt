package com.speech.network.model.speech

import android.annotation.SuppressLint
import com.speech.domain.model.speech.Audience
import com.speech.domain.model.speech.Filler
import com.speech.domain.model.speech.RepeatedWord
import com.speech.domain.model.speech.Silence
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechDetail
import com.speech.domain.model.speech.SpeechType
import com.speech.domain.model.speech.Venue
import com.speech.domain.model.speech.VerbalAnalysis
import kotlinx.serialization.Serializable

@Serializable
data class GetVerbalAnalysisResponse(
    val wordCnt: Int,
    val syllableCnt: Int,
    val fillers: List<FillerResponse>,
    val repeatedWords: List<RepeatedWordResponse>,
    val silences: List<SilenceResponse>
) {
    fun toDomain(): VerbalAnalysis =
        VerbalAnalysis(
            wordCount = wordCnt,
            syllableCount = syllableCnt,
            fillers = fillers.map { it.toDomain() },
            repeatedWords = repeatedWords.map { it.toDomain() },
            silences = silences.map { it.toDomain() }
        )
}

@Serializable
data class FillerResponse(
    val word: String,
    val timestamps: List<Int>
) {
    fun toDomain(): Filler =
        Filler(
            word = word,
            timestamps = timestamps
        )
}

@Serializable
data class RepeatedWordResponse(
    val word: String,
    val count: Int
) {
    fun toDomain(): RepeatedWord =
        RepeatedWord(
            word = word,
            count = count
        )
}

@Serializable
data class SilenceResponse(
    val duration: Int,
    val startTime: Int,
    val endTime: Int,
    val wordBefore: String,
    val wordAfter: String
) {
    fun toDomain(): Silence =
        Silence(
            duration = duration,
            startTime = startTime,
            endTime = endTime,
            wordBefore = wordBefore,
            wordAfter = wordAfter
        )
}
