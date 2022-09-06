package com.mckernant1.lol.esports.api.config

import com.google.gson.Gson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GsonModule {

    @Bean
    fun gson(): Gson = Gson()

}
