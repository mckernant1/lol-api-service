package com.mckernant1.lol.esports.api.util

import com.github.mckernant1.lol.esports.api.models.Tournament
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit


fun Tournament.startDateAsDate(): Instant? {
    return try {
        SimpleDateFormat("yyyy-MM-dd").parse(this.startDate).toInstant()
    } catch (e: Exception) {
        null
    }
}

private fun Instant?.orEpoch(): Instant = this ?: Instant.EPOCH

fun Tournament.endDateAsDate(): Instant? {
    return try {
        SimpleDateFormat("yyyy-MM-dd").parse(this.endDate).toInstant()
    } catch (e: Exception) {
        null
    }
}

fun Tournament.isOngoing(): Boolean {
    val now: Instant = Instant.now().truncatedTo(ChronoUnit.DAYS)
    return this.startDateAsDate().orEpoch() <= now && now <= this.endDateAsDate().orEpoch()
}
