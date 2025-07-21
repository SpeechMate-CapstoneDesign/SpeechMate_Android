package com.speech.domain.model.auth

// 언어적 발표 목표
enum class VerbalSkill(val label: String) {
    REDUCE_HESITATION("발표 중 머뭇거림 줄이기"),
    REDUCE_NERVOUSNESS("긴장 줄이기"),
    CONFIDENT_SPEAKING("자신감있게 발표하기"),
    APPROPRIATE_PACE("적절한 속도로 말하기"),
    CLEAR_PRONUNCIATION("발음 명확히 하기"),
    REDUCE_FILLER_WORDS("간투어(\"음\", \"어\" 등) 줄이기"),
}

// 비언어적 발표 목표
enum class NonVerbalSkill(val label: String) {
    NATURAL_FACIAL_EXPRESSION("자연스러운 표정으로 말하기"),
    MAINTAIN_GOOD_POSTURE("바른 자세 유지하기"),
    REDUCE_UNNECESSARY_HAND_GESTURES("불필요한 손동작 줄이기"),
    REDUCE_UNNECESSARY_HEAD_MOVEMENT("불필요한 머리 움직임 줄이기"),
    CONTROL_ARM_MOVEMENTS("팔동작 절제하기"),
}