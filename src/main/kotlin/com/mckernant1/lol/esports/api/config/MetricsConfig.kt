package com.mckernant1.lol.esports.api.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.mckernant1.commons.metrics.Dimension
import com.mckernant1.commons.metrics.Metrics
import com.mckernant1.commons.metrics.impls.CloudWatchMetrics
import com.mckernant1.commons.metrics.impls.NoopMetrics
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient
import java.net.InetAddress

@Configuration
class MetricsConfig {

    companion object {
        private const val NAMESPACE = "Lol-Esports/Api"
    }

    @Bean
    fun defaultDimensions(): Set<Dimension> = setOf(
        Dimension("Host", "${ProcessHandle.current().pid()}@${InetAddress.getLocalHost().hostName}"),

        )

    @Bean
    @Profile("prod")
    fun metrics(
        cloudWatchClient: CloudWatchClient,
        dimensions: Set<Dimension>
    ): Metrics = CloudWatchMetrics(NAMESPACE, cloudWatchClient, dimensions)


    @Bean
    @Profile("!prod")
    fun devMetrics(
        dimensions: Set<Dimension>,
        mapper: ObjectMapper
    ): Metrics = NoopMetrics(NAMESPACE, dimensions, mapper)

}
