package com.speech.common.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun formatDuration(duration: Duration) =
    duration.toComponents { minutes, seconds, _ ->
        "%02d:%02d".format(minutes, seconds)
    }

fun getProgress(
    current: Duration,
    total: Duration,
): Float =
    if (total == 0.milliseconds) 0f
    else (current.inWholeMilliseconds.toFloat() / total.inWholeMilliseconds.toFloat())


