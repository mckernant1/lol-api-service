package com.mckernant1.lol.esports.api.ctrl

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.mckernant1.commons.extensions.boolean.falseIfNull
import com.mckernant1.lol.esports.api.models.Tournament
import com.mckernant1.lol.esports.api.svc.LeagueService
import com.mckernant1.lol.esports.api.svc.TournamentService
import com.mckernant1.lol.esports.api.util.endDateAsDate
import com.mckernant1.lol.esports.api.util.isOngoing
import com.mckernant1.lol.esports.api.util.startDateAsDate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@RestController
class TournamentController(
    private val tournamentService: TournamentService,
    private val leagueService: LeagueService
) {

    @GetMapping("/ongoing-tournaments")
    fun getOngoingTournaments(): List<Tournament> {
        return tournamentService.scanTournaments()
            .filter {
                it.startDateAsDate()?.isBefore(Instant.now()).falseIfNull()
                        && it.endDateAsDate()?.isAfter(Instant.now()).falseIfNull()
            }.toList()
    }


    @GetMapping("/tournament/{tournamentId}")
    fun getTournament(@PathVariable tournamentId: String): Tournament {
        return tournamentService.getTournamentById(tournamentId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "The tournament with id $tournamentId does not exist"
        )
    }

    private val tournamentsForLeagueCache: LoadingCache<String, List<Tournament>> = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(60))
        .build(
            CacheLoader.from { leagueId ->
                tournamentService
                    .getTournamentsForLeague(leagueId)
                    .toList()
            })

    @GetMapping("/tournaments/{leagueId}")
    fun getTournamentForLeague(@PathVariable leagueId: String): List<Tournament> {
        leagueService.assertLeagueExists(leagueId)

        return tournamentsForLeagueCache[leagueId]
    }

    /**
     * Returns either the ongoing tournament, the next up tournament or null, if none
     */
    @GetMapping("/most-recent-tournament/{leagueId}")
    fun getMostRecentTournament(@PathVariable leagueId: String): Tournament? {
        leagueService.assertLeagueExists(leagueId)

        var tourneys = tournamentService.getTournamentsForLeague(leagueId)
            .filter {
                it.startDateAsDate()
                    ?.minus(7, ChronoUnit.DAYS)
                    ?.isBefore(Instant.now())
                    ?: false
            }.sortedByDescending { it.startDateAsDate()!! }

        // Worlds has unofficial tournaments associated with it for some reason. So we should remove it
        if (leagueId.equals("WCS", ignoreCase = true)) {
            tourneys = tourneys.filter { it.isOfficial.falseIfNull() }
        }

        return tourneys.find { it.isOngoing() } ?: tourneys.firstOrNull()
    }

}
