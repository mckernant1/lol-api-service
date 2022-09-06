package com.mckernant1.lol.esports.api.dao

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataAccess {

    @Bean
    fun ddbV1Client(): AmazonDynamoDB = AmazonDynamoDBClient.builder().build()

}
