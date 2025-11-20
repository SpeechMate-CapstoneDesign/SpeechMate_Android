package com.speech.domain.model.speech

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class NonVerbalAnalysis(
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
    val timestamps: List<TimeRange>,
)

data class TimeRange(
    val startTime: Duration,
    val endTime: Duration,
)

fun createSampleNonVerbalAnalysis(): NonVerbalAnalysis {
    // 1. 머리 동작 데이터
    val headBehaviors = listOf(
        Behavior(
            name = "고개 흔들기",
            count = 7,
            timestamps = listOf(
                TimeRange(startTime = 15.seconds, endTime = 18.seconds),
                TimeRange(startTime = 120.seconds, endTime = 123.seconds),
                TimeRange(startTime = 190.seconds, endTime = 192.seconds),
                TimeRange(startTime = 215.seconds, endTime = 218.seconds),
            ),
        ),
        Behavior(
            name = "고개 숙이기",
            count = 5,
            timestamps = listOf(
                TimeRange(startTime = 25.seconds, endTime = 28.seconds),
                TimeRange(startTime = 102.seconds, endTime = 105.seconds),
                TimeRange(startTime = 200.seconds, endTime = 203.seconds),
            ),
        ),
        Behavior(
            name = "천장 보기",
            count = 3,
            timestamps = listOf(
                TimeRange(startTime = 35.seconds, endTime = 37.seconds),
                TimeRange(startTime = 175.seconds, endTime = 177.seconds),
            ),
        ),
    )

    // 2. 얼굴 표정 데이터
    val faceBehaviors = listOf(
        Behavior(
            name = "자주 눈 깜빡이기",
            count = 12,
            timestamps = listOf(
                TimeRange(startTime = 10.seconds, endTime = 15.seconds),
                TimeRange(startTime = 30.seconds, endTime = 35.seconds),
                TimeRange(startTime = 95.seconds, endTime = 100.seconds),
                TimeRange(startTime = 225.seconds, endTime = 230.seconds),
            ),
        ),
        Behavior(
            name = "입술 깨물기",
            count = 4,
            timestamps = listOf(
                TimeRange(startTime = 42.seconds, endTime = 44.seconds),
                TimeRange(startTime = 88.seconds, endTime = 90.seconds),
                TimeRange(startTime = 188.seconds, endTime = 190.seconds),
            ),
        ),
    )

    // 3. 자세 데이터
    val postureBehaviors = listOf(
        Behavior(
            name = "비스듬한 자세",
            count = 8,
            timestamps = listOf(
                TimeRange(startTime = 20.seconds, endTime = 35.seconds),
                TimeRange(startTime = 60.seconds, endTime = 75.seconds),
                TimeRange(startTime = 125.seconds, endTime = 140.seconds),
                TimeRange(startTime = 205.seconds, endTime = 215.seconds),
                TimeRange(startTime = 225.seconds, endTime = 235.seconds),
            ),
        ),
        Behavior(
            name = "무화과 잎 자세",
            count = 6,
            timestamps = listOf(
                TimeRange(startTime = 12.seconds, endTime = 18.seconds),
            ),
        ),
        Behavior(
            name = "경직된 차려 자세",
            count = 3,
            timestamps = listOf(
                TimeRange(startTime = 38.seconds, endTime = 45.seconds),
                TimeRange(startTime = 175.seconds, endTime = 182.seconds),
            ),
        ),
    )

    // 4. 팔 동작 데이터
    val armsBehaviors = listOf(
        Behavior(
            name = "팔짱끼기",
            count = 9,
            timestamps = listOf(
                TimeRange(startTime = 8.seconds, endTime = 15.seconds),
                TimeRange(startTime = 32.seconds, endTime = 40.seconds),
                TimeRange(startTime = 58.seconds, endTime = 65.seconds),
                TimeRange(startTime = 218.seconds, endTime = 225.seconds),
            ),
        ),
        Behavior(
            name = "뒷짐",
            count = 2,
            timestamps = listOf(
                TimeRange(startTime = 78.seconds, endTime = 82.seconds),
                TimeRange(startTime = 152.seconds, endTime = 156.seconds),
            ),
        ),
    )

    // 5. 손 동작 데이터
    val handsBehaviors = listOf(
        Behavior(
            name = "턱 만지기",
            count = 7,
            timestamps = listOf(
                TimeRange(startTime = 22.seconds, endTime = 25.seconds),
                TimeRange(startTime = 55.seconds, endTime = 58.seconds),
                TimeRange(startTime = 88.seconds, endTime = 91.seconds),
            ),
        ),
        Behavior(
            name = "코 만지기",
            count = 5,
            timestamps = listOf(
                TimeRange(startTime = 28.seconds, endTime = 30.seconds),
                TimeRange(startTime = 68.seconds, endTime = 70.seconds),
                TimeRange(startTime = 202.seconds, endTime = 204.seconds),
            ),
        ),
        Behavior(
            name = "귀 만지기 (왼쪽)",
            count = 3,
            timestamps = listOf(
                TimeRange(startTime = 42.seconds, endTime = 44.seconds),
                TimeRange(startTime = 98.seconds, endTime = 100.seconds),
                TimeRange(startTime = 178.seconds, endTime = 180.seconds),
            ),
        ),
        Behavior(
            name = "귀 만지기 (오른쪽)",
            count = 4,
            timestamps = listOf(
                TimeRange(startTime = 35.seconds, endTime = 37.seconds),
                TimeRange(startTime = 72.seconds, endTime = 74.seconds),
            ),
        ),
        Behavior(
            name = "이마 만지기",
            count = 6,
            timestamps = listOf(
                TimeRange(startTime = 18.seconds, endTime = 20.seconds),
                TimeRange(startTime = 62.seconds, endTime = 64.seconds),
                TimeRange(startTime = 95.seconds, endTime = 97.seconds),
            ),
        ),
        Behavior(
            name = "머리 만지기",
            count = 8,
            timestamps = listOf(
                TimeRange(startTime = 12.seconds, endTime = 15.seconds),
                TimeRange(startTime = 75.seconds, endTime = 78.seconds),
                TimeRange(startTime = 142.seconds, endTime = 145.seconds),
                TimeRange(startTime = 172.seconds, endTime = 175.seconds),
                TimeRange(startTime = 228.seconds, endTime = 231.seconds),
            ),
        ),
        Behavior(
            name = "손 비비기",
            count = 10,
            timestamps = listOf(
                TimeRange(startTime = 5.seconds, endTime = 8.seconds),
                TimeRange(startTime = 38.seconds, endTime = 41.seconds),
                TimeRange(startTime = 175.seconds, endTime = 178.seconds),
                TimeRange(startTime = 192.seconds, endTime = 195.seconds),
                TimeRange(startTime = 235.seconds, endTime = 238.seconds),
            ),
        ),
    )

    return NonVerbalAnalysis(
        totalCount = 12,
        results = mapOf(
            BehaviorGroup.HEAD to headBehaviors,
            BehaviorGroup.FACE to faceBehaviors,
            BehaviorGroup.POSTURE to postureBehaviors,
            BehaviorGroup.ARMS to armsBehaviors,
            BehaviorGroup.HANDS to handsBehaviors,
        ),
    )
}
