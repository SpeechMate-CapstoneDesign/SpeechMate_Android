package com.speech.domain.model.speech

data class VerbalAnalysis(
    val wordCount: Int = 0,
    val syllableCount: Int = 0,
    val fillers: List<Filler> = emptyList(),
    val repeatedWords: List<RepeatedWord> = emptyList(),
    val silences: List<Silence> = emptyList(),
)

data class Filler(
    val word: String,
    val timestamps: List<Int>,
)

data class RepeatedWord(
    val word: String,
    val count: Int,
)

data class Silence(
    val duration: Int,
    val startTime: Int,
    val endTime: Int,
    val wordBefore: String,
    val wordAfter: String,
)
