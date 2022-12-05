package com.mckernant1.lol.esports.api.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule


val objectMapper by lazy {
    ObjectMapper().apply {
        registerModule(
            SimpleModule().addSerializer(AttributeValueSerializer.INSTANCE)
        )
    }
}

inline fun <reified T> Map<String, Any>.toObject(): T =
    objectMapper.convertValue(this, T::class.java)

inline fun <reified T : Any> Sequence<Map<String, Any>>.mapToObject(): Sequence<T> = map {
    objectMapper.convertValue(it, T::class.java)
}
