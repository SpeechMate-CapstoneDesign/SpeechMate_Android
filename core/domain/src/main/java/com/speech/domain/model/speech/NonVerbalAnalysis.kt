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
    val timestamps: List<Duration>,
)


fun createSampleNonVerbalAnalysis(): NonVerbalAnalysis {
    // 1. 머리 동작 데이터
    val headBehaviors = listOf(
        Behavior(
            name = "고개 흔들기",
            count = 7,
            timestamps = listOf(
                15.seconds,
                120.seconds,
                190.seconds,
                215.seconds,
            ),
        ),
        Behavior(
            name = "고개 숙이기",
            count = 5,
            timestamps = listOf(
                25.seconds,
                102.seconds,
                200.seconds,
            ),
        ),
        Behavior(
            name = "천장 보기",
            count = 3,
            timestamps = listOf(
                35.seconds,
                175.seconds,
            ),
        ),
    )

    // 2. 얼굴 표정 데이터
    val faceBehaviors = listOf(
        Behavior(
            name = "자주 눈 깜빡이기",
            count = 12,
            timestamps = listOf(
                10.seconds,
                30.seconds,
                95.seconds,
                225.seconds,
            ),
        ),
        Behavior(
            name = "입술 깨물기",
            count = 4,
            timestamps = listOf(
                42.seconds,
                88.seconds,
                188.seconds,
            ),
        ),
    )

    // 3. 자세 데이터
    val postureBehaviors = listOf(
        Behavior(
            name = "비스듬한 자세",
            count = 8,
            timestamps = listOf(
                20.seconds,
                60.seconds,
                125.seconds,
                205.seconds,
                225.seconds,
            ),
        ),
        Behavior(
            name = "무화과 잎 자세",
            count = 6,
            timestamps = listOf(
                12.seconds,
            ),
        ),
        Behavior(
            name = "경직된 차려 자세",
            count = 3,
            timestamps = listOf(
                38.seconds,
                175.seconds,
            ),
        ),
    )

    // 4. 팔 동작 데이터
    val armsBehaviors = listOf(
        Behavior(
            name = "팔짱끼기",
            count = 9,
            timestamps = listOf(
                8.seconds,
                32.seconds,
                58.seconds,
                218.seconds,
            ),
        ),
        Behavior(
            name = "뒷짐",
            count = 2,
            timestamps = listOf(
                78.seconds,
                152.seconds,
            ),
        ),
    )

    // 5. 손 동작 데이터
    val handsBehaviors = listOf(
        Behavior(
            name = "턱 만지기",
            count = 7,
            timestamps = listOf(
                22.seconds,
                55.seconds,
                88.seconds,
            ),
        ),
        Behavior(
            name = "코 만지기",
            count = 5,
            timestamps = listOf(
                28.seconds,
                68.seconds,
                202.seconds,
            ),
        ),
        Behavior(
            name = "귀 만지기 (왼쪽)",
            count = 3,
            timestamps = listOf(
                42.seconds,
                98.seconds,
                178.seconds,
            ),
        ),
        Behavior(
            name = "귀 만지기 (오른쪽)",
            count = 4,
            timestamps = listOf(
                35.seconds,
                72.seconds,
            ),
        ),
        Behavior(
            name = "이마 만지기",
            count = 6,
            timestamps = listOf(
                18.seconds,
                62.seconds,
                95.seconds,
            ),
        ),
        Behavior(
            name = "머리 만지기",
            count = 8,
            timestamps = listOf(
                12.seconds,
                75.seconds,
                142.seconds,
                172.seconds,
                228.seconds,
            ),
        ),
        Behavior(
            name = "손 비비기",
            count = 10,
            timestamps = listOf(
                5.seconds,
                38.seconds,
                175.seconds,
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



