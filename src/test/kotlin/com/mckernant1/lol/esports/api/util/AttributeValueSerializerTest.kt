package com.mckernant1.lol.esports.api.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.mckernant1.lol.esports.api.models.Team
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class AttributeValueSerializerTest {

    private val mapper = ObjectMapper().apply {
        registerModule(
            SimpleModule().addSerializer(AttributeValueSerializer.INSTANCE)
        )
    }

    @Test
    fun testNull() {
        assertNull(mapper.convertValue(null, Team::class.java))
    }

}
