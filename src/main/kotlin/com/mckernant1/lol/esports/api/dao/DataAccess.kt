package com.mckernant1.lol.esports.api.dao

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
class DataAccess {
    @Bean
    fun ddbClient(): DynamoDbClient = DynamoDbClient.create()

}
