package com.mckernant1.lol.esports.api.scheduled

import com.mckernant1.commons.logging.Slf4j.logger
import com.mckernant1.commons.metrics.Metrics
import com.mckernant1.commons.metrics.guava.CacheMetrics.addCacheStats
import com.mckernant1.lol.esports.api.metrics.PeriodicSubmitCacheStats
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class MetricsSubmission(
    private val classesToSubmit: List<PeriodicSubmitCacheStats>,
    metrics: Metrics
) {

    private val logger = logger()
    private val metrics = metrics.newMetricsForClass(this::class)

    companion object {
        private const val CACHE_NAME = "CacheName"
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    fun submitMetrics() {
        logger.info("Submitting periodic metrics")
        for ((cacheName, cache) in classesToSubmit.flatMap { it.caches }) {
            metrics.withNewMetrics(CACHE_NAME to cacheName) {
                it.addCacheStats(cache.stats())
            }
        }
    }
}
