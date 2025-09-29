package com.speech.domain.model.speech

import kotlin.time.Duration

data class VerbalAnalysis(
    val wordCount: Int = 0,
    val syllableCount: Int = 0,
    val fillers: List<Filler> = emptyList(),
    val repeatedWords: List<RepeatedWord> = emptyList(),
    val silences: List<Silence> = emptyList(),
)

data class Filler(
    val word: String,
    val timestamps: List<Duration>,
)

data class RepeatedWord(
    val word: String,
    val count: Int,
)

data class Silence(
    val duration: Duration,
    val startTime: Duration,
    val endTime: Duration,
    val wordBefore: String,
    val wordAfter: String,
)
