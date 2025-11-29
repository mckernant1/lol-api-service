package com.mckernant1.lol.esports.api.grpc

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.mckernant1.commons.extensions.boolean.falseIfNull
import com.mckernant1.lol.esports.api.metrics.PeriodicSubmitCacheStats
import com.mckernant1.lol.esports.api.models.Tournament
import com.mckernant1.lol.esports.api.svc.LeagueService
import com.mckernant1.lol.esports.api.svc.TournamentService
import com.mckernant1.lol.esports.api.util.endDateAsDate
import com.mckernant1.lol.esports.api.util.isOngoing
import com.mckernant1.lol.esports.api.util.startDateAsDate
import com.mckernant1.lol.esports.api.util.toGrpc
import com.mckernant1.lol.tournament.GetMostRecentTournamentRequest
import com.mckernant1.lol.tournament.GetMostRecentTournamentResponse
import com.mckernant1.lol.tournament.GetOngoingTournamentsRequest
import com.mckernant1.lol.tournament.GetOngoingTournamentsResponse
import com.mckernant1.lol.tournament.GetTournamentRequest
import com.mckernant1.lol.tournament.GetTournamentResponse
import com.mckernant1.lol.tournament.GetTournamentsForLeagueRequest
import com.mckernant1.lol.tournament.GetTournamentsForLeagueResponse
import com.mckernant1.lol.tournament.TournamentServiceGrpcKt
import org.springframework.grpc.server.service.GrpcService
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit


@GrpcService
class TournamentGrpcService(
    private val tournamentService: TournamentService,
    private val leagueService: LeagueService
) : TournamentServiceGrpcKt.TournamentServiceCoroutineImplBase(), PeriodicSubmitCacheStats {

    override suspend fun getOngoingTournaments(request: GetOngoingTournamentsRequest): GetOngoingTournamentsResponse {
        val tournaments = tournamentService.scanTournaments()
            .filter {
                it.startDateAsDate()?.isBefore(Instant.now()).falseIfNull()
                        && it.endDateAsDate()?.isAfter(Instant.now()).falseIfNull()
            }
            .map { it.toGrpc() }
            .toList()

        return GetOngoingTournamentsResponse.newBuilder()
            .addAllTournaments(tournaments)
            .build()
    }


    override suspend fun getTournament(request: GetTournamentRequest): GetTournamentResponse {
        val t = tournamentService.getTournamentById(request.tournamentId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "The tournament with id ${request.tournamentId} does not exist"
        )

        return GetTournamentResponse.newBuilder()
            .setTournament(t.toGrpc())
            .build()
    }

    private val tournamentsForLeagueCache: LoadingCache<String, List<Tournament>> = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofHours(12))
        .recordStats()
        .build(
            CacheLoader.from { leagueId ->
                tournamentService
                    .getTournamentsForLeague(leagueId)
                    .toList()
            })

    override suspend fun getTournamentsForLeague(request: GetTournamentsForLeagueRequest): GetTournamentsForLeagueResponse {
        leagueService.assertLeagueExists(request.leagueId)

        return GetTournamentsForLeagueResponse.newBuilder()
            .addAllTournaments(tournamentService.getTournamentsForLeague(request.leagueId).map { it.toGrpc() }.toList())
            .build()
    }

    /**
     * Returns either the ongoing tournament, the next up tournament or null, if none
     */
    override suspend fun getMostRecentTournament(request: GetMostRecentTournamentRequest): GetMostRecentTournamentResponse {
        leagueService.assertLeagueExists(request.leagueId)

        var tourneys = tournamentService.getTournamentsForLeague(request.leagueId)
            .filter {
                it.startDateAsDate()
                    ?.minus(7, ChronoUnit.DAYS)
                    ?.isBefore(Instant.now())
                    ?: false
            }.sortedByDescending { it.startDateAsDate()!! }

        // Worlds has unofficial tournaments associated with it for some reason. So we should remove it
        if (request.leagueId.equals("WCS", ignoreCase = true)) {
            tourneys = tourneys.filter { it.isOfficial.falseIfNull() }
        }

        val mostRecent = tourneys.find { it.isOngoing() } ?: tourneys.firstOrNull()

        return if (mostRecent != null) {
            GetMostRecentTournamentResponse.newBuilder()
                .setTournament(mostRecent.toGrpc())
                .build()
        } else {
            GetMostRecentTournamentResponse.getDefaultInstance()
        }
    }

    override val caches: List<Pair<String, LoadingCache<out Any, out Any>>> = listOf(
        "TournamentForLeagueGrpcCache" to tournamentsForLeagueCache
    )

}
