package com.mckernant1.lol.esports.api.config

import com.mckernant1.commons.metrics.Dimension
import com.mckernant1.commons.metrics.Metrics
import com.mckernant1.commons.metrics.impls.CloudWatchMetrics
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient

@Configuration
class MetricsConfig {

    companion object {
        private const val NAMESPACE = "Lol-Esports/Api"
    }

    @Bean
    fun defaultDimensions(): Set<Dimension> = setOf()

    @Bean
    fun metrics(
        cloudWatchClient: CloudWatchClient,
        dimensions: Set<Dimension>
    ): Metrics = CloudWatchMetrics(NAMESPACE, cloudWatchClient, dimensions)
}
