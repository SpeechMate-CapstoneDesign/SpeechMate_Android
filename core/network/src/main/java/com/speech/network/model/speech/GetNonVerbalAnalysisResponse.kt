package com.speech.network.model.speech

import com.speech.domain.model.speech.AnalysisStatus
import com.speech.domain.model.speech.Behavior
import com.speech.domain.model.speech.BehaviorGroup
import com.speech.domain.model.speech.NonVerbalAnalysis
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Serializable
data class GetNonVerbalAnalysisResponse(
    val analysisStatus: String,
    val result: NonVerbalAnalysisResult?,
) {
    fun toDomain(): NonVerbalAnalysis {
        if (result == null) {
            return NonVerbalAnalysis(
                status = AnalysisStatus.fromString(analysisStatus),
                totalCount = 0,
                results = emptyMap(),
            )
        }

        val behaviorsByGroup = result.results.mapNotNull { (groupKey, behaviors) ->
            val group = when (groupKey) {
                "HEAD" -> BehaviorGroup.HEAD
                "ARMS" -> BehaviorGroup.ARMS
                "HANDS" -> BehaviorGroup.HANDS
                "POSTURE" -> BehaviorGroup.POSTURE
                "FACE" -> BehaviorGroup.FACE
                else -> {
                    null
                }
            }

            group?.let {
                it to behaviors.map { behavior -> behavior.toDomain() }
            }
        }.toMap()

        return NonVerbalAnalysis(
            status = AnalysisStatus.fromString(analysisStatus),
            totalCount = result.totalCount,
            results = behaviorsByGroup,
        )
    }
}

@Serializable
data class NonVerbalAnalysisResult(
    val totalCount: Int,
    val results: Map<String, List<BehaviorResponse>>,
)

@Serializable
data class BehaviorResponse(
    val name: String,
    val count: Int,
    val timestamps: List<String>,
) {
    fun toDomain(): Behavior = Behavior(
        name = name,
        count = count,
        timestamps = timestamps.map { it.parseTimestamp() },
    )
}

private fun String.parseTimestamp(): Duration {
    val parts = split(":")
    require(parts.size == 3) { "Invalid timestamp format: $this" }

    val hours = parts[0].toInt()
    val minutes = parts[1].toInt()
    val seconds = parts[2].toInt()

    return hours.hours + minutes.minutes + seconds.seconds
}



