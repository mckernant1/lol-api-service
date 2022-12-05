package com.mckernant1.lol.esports.api.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.mckernant1.lol.esports.api.util.AttributeValueSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {
    @Bean
    fun provideObjectMapper(): ObjectMapper = ObjectMapper().apply {
        registerModule(
            SimpleModule().addSerializer(AttributeValueSerializer.INSTANCE)
        )
    }

}
