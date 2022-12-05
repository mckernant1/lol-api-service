package com.mckernant1.lol.esports.api.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.github.mckernant1.logging.Slf4j.logger
import org.slf4j.Logger
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructMap
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.io.IOException
import java.math.BigDecimal

/**
 * Taken from https://gist.github.com/nickman/991dea808b6f43e7346d6d570f3c9ffe
 */

class AttributeValueSerializer private constructor() : JsonSerializer<AttributeValue>() {

    private val logger: Logger = logger()

    @Throws(IOException::class)
    override fun serialize(av: AttributeValue?, gen: JsonGenerator, serializers: SerializerProvider) {
        if (av == null) {
            gen.writeNull()
        } else {
            if (av.m() != EMPTY_ATTR_MAP) {
                gen.writeStartObject()
                val map = av.m()
                for ((key, value) in map) {
                    gen.writeFieldName(key)
                    serialize(value, gen, serializers)
                }
                gen.writeEndObject()
            } else if (av.l() != EMPTY_ATTR_LIST) {
                val list = av.l()
                gen.writeStartArray()
                for (a in list) {
                    serialize(a, gen, serializers)
                }
                gen.writeEndArray()
            } else if (av.s() != null) {
                gen.writeString(av.s())
            } else if (av.n() != null) {
                gen.writeNumber(BigDecimal(av.n()))
            } else if (av.bool() != null) {
                gen.writeBoolean(av.bool())
            } else if (av.nul() != null && av.nul()) {
                gen.writeNull()
            } else if (av.b() != null) {
                gen.writeBinary(av.b().asByteArray())
            } else if (av.ss() != EMPTY_ATTR_LIST) {
                val list = av.ss().toTypedArray()
                gen.writeArray(list, 0, list.size)
            } else if (av.bs() != EMPTY_ATTR_LIST) {
                val list = av.ss().toTypedArray()
                gen.writeArray(list, 0, list.size)
            } else if (av.ns() != EMPTY_ATTR_LIST) {
                val list = av.ss().toTypedArray()
                gen.writeArray(list, 0, list.size)
            } else if (av.nul() != null) {
                gen.writeNull()
            } else {
                logger.error("MISSED DATA TYPE: $av");
            }
        }
    }

    override fun handledType(): Class<AttributeValue> {
        return AttributeValue::class.java
    }

    companion object {
        val EMPTY_ATTR_MAP: DefaultSdkAutoConstructMap<*, *> = DefaultSdkAutoConstructMap.getInstance<Any, Any>()
        val EMPTY_ATTR_LIST: DefaultSdkAutoConstructList<*> = DefaultSdkAutoConstructList.getInstance<Any>()
        val INSTANCE: JsonSerializer<AttributeValue> = AttributeValueSerializer()
    }
}
