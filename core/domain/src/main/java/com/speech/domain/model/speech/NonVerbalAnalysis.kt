package com.speech.domain.model.speech

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class NonVerbalAnalysis(
    val status: AnalysisStatus,
    val totalCount: Int = 0,
    val results: Map<BehaviorGroup, List<Behavior>> = emptyMap(),
)

enum class BehaviorGroup(val label: String, val emoji: String) {
    HEAD("머리", "🙇"),
    ARMS("팔", "💪"),
    HANDS("손", "👐"),
    POSTURE("자세", "🧍"),
    FACE("얼굴", "🙂"),
}

data class Behavior(
    val name: String,
    val count: Int,
    val timestamps: List<Duration>,
)

enum class AnalysisStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED;

    companion object {
        fun fromString(status: String): AnalysisStatus {
            return when (status) {
                "IN_PROGRESS" -> IN_PROGRESS
                "COMPLETED" -> COMPLETED
                else -> FAILED
            }
        }
    }
}




