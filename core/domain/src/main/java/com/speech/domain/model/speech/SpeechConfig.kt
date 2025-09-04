package com.speech.domain.model.speech


data class SpeechConfig(
    val fileName: String = "",
    val speechType: SpeechType? = null,
    val audience: Audience? = null,
    val venue: Venue? = null,
) {
    val isValid: Boolean
        get() = fileName.isNotBlank() && speechType != null && audience != null && venue != null
}

enum class SpeechType(val label: String) {
    BUSINESS_PRESENTATION("비즈니스 프레젠테이션"),
    EVENT("행사"),
    ACADEMIC_PRESENTATION("학술 발표"),
    PRACTICE("단순 연습");

    companion object {
        fun fromString(name: String): SpeechType =
            entries.find { it.name.equals(name) } ?: PRACTICE
    }
}

enum class Audience(val label: String) {
    BEGINNER("초보자"),
    INTERMEDIATE("중급자"),
    EXPERT("전문가");

    companion object {
        fun fromString(name: String): Audience =
            entries.find { it.name.equals(name) } ?: BEGINNER
    }
}

enum class Venue(val label: String) {
    CONFERENCE_ROOM("회의실"),
    EVENT_HALL("행사장"),
    ONLINE("온라인"),
    LECTURE_HALL("강의실 / 교실");

    companion object {
        fun fromString(name: String): Venue =
            entries.find { it.name.equals(name) } ?: ONLINE
    }
}
