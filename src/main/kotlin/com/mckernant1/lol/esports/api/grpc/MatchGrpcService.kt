package com.mckernant1.lol.esports.api.grpc

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.mckernant1.lol.esports.api.metrics.PeriodicSubmitCacheStats
import com.mckernant1.lol.esports.api.svc.MatchService
import com.mckernant1.lol.esports.api.svc.TournamentService
import com.mckernant1.lol.esports.api.util.toGrpc
import com.mckernant1.lol.matches.GetMatchesForTournamentRequest
import com.mckernant1.lol.matches.GetMatchesForTournamentResponse
import com.mckernant1.lol.matches.Match
import com.mckernant1.lol.matches.MatchServiceGrpcKt
import net.devh.boot.grpc.server.service.GrpcService
import java.time.Duration

@GrpcService
class MatchGrpcService(
    private val matchService: MatchService,
    private val tournamentService: TournamentService
) : MatchServiceGrpcKt.MatchServiceCoroutineImplBase(), PeriodicSubmitCacheStats {


    override suspend fun getMatchesForTournament(request: GetMatchesForTournamentRequest): GetMatchesForTournamentResponse {

        tournamentService.verifyTournamentExists(request.tournamentId)

        return GetMatchesForTournamentResponse.newBuilder()
            .addAllMatches(matchesForTournamentCache[request.tournamentId])
            .build()

    }

    private val matchesForTournamentCache: LoadingCache<String, List<Match>> = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(30))
        .recordStats()
        .build(CacheLoader.from { tournamentId ->
            matchService.getMatchesForTournament(tournamentId)
                .map { it.toGrpc() }
                .toList()
        })

    override val caches: List<Pair<String, LoadingCache<out Any, out Any>>> = listOf(
        "MatchesForTournamentGrpcCache" to matchesForTournamentCache,
    )

}
