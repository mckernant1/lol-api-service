package com.mckernant1.lol.esports.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
class AwsConfig {

    @Bean
    fun ddbClient(): DynamoDbClient = DynamoDbClient.create()

    @Bean
    fun cloudwatchClient(): CloudWatchClient = CloudWatchClient.create()
}
