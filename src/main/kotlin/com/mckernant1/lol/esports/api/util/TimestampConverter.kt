package com.mckernant1.lol.esports.api.util

import com.google.protobuf.Timestamp
import java.time.Instant

object TimestampConverter {

    fun Instant.toTimestamp(): Timestamp = Timestamp.newBuilder()
        .setSeconds(epochSecond)
        .setNanos(nano)
        .build()

}
