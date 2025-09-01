package com.speech.domain.model.speech

enum class FeedbackTab(val label : String) {
    SPEECH_CONFIG("발표 설정"),
    SCRIPT("대본"),
    SCRIPT_ANALYSIS("대본 분석"),
    VERBAL_ANALYSIS("언어적 분석"),
    NON_VERBAL_ANALYSIS("비언어적 분석")
}
