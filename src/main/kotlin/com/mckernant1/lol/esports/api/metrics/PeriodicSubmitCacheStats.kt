package com.mckernant1.lol.esports.api.metrics

import com.google.common.cache.LoadingCache

interface PeriodicSubmitCacheStats {

    val caches: List<Pair<String, LoadingCache<out Any, out Any>>>

}
