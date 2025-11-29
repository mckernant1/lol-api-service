package com.mckernant1.lol.esports.api.grpc

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.mckernant1.lol.esports.api.metrics.PeriodicSubmitCacheStats
import com.mckernant1.lol.esports.api.models.League
import com.mckernant1.lol.esports.api.svc.LeagueService
import com.mckernant1.lol.esports.api.util.toGrpc
import com.mckernant1.lol.leagues.GetLeagueRequest
import com.mckernant1.lol.leagues.GetLeagueResponse
import com.mckernant1.lol.leagues.LeagueServiceGrpcKt
import com.mckernant1.lol.leagues.ListLeaguesRequest
import com.mckernant1.lol.leagues.ListLeaguesResponse
import org.springframework.grpc.server.service.GrpcService
import java.time.Duration


@GrpcService
class LeagueGrpcService(
    private val leagueService: LeagueService
) : LeagueServiceGrpcKt.LeagueServiceCoroutineImplBase(), PeriodicSubmitCacheStats {

    override suspend fun listLeagues(request: ListLeaguesRequest): ListLeaguesResponse {
        return ListLeaguesResponse.newBuilder()
            .addAllLeagues(leagueService.scanLeagues().map { it.toGrpc() }.toList())
            .build()
    }

    override suspend fun getLeague(request: GetLeagueRequest): GetLeagueResponse {
        leagueService.assertLeagueExists(request.leagueId)

        return GetLeagueResponse.newBuilder()
            .setLeague(leagueCache[request.leagueId].toGrpc())
            .build()
    }


    private val leagueCache: LoadingCache<String, League> = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofMinutes(30))
        .expireAfterWrite(Duration.ofHours(12))
        .recordStats()
        .build(CacheLoader.from { leagueId ->
            leagueService.getLeague(leagueId)!!
        })

    override val caches: List<Pair<String, LoadingCache<out Any, out Any>>> = listOf(
        "LeagueGrpcCache" to leagueCache
    )

}
