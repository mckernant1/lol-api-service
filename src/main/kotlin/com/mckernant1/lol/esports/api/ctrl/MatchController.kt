package com.mckernant1.lol.esports.api.ctrl

import com.mckernant1.commons.extensions.boolean.falseIfNull
import com.mckernant1.commons.extensions.time.Instants.isBeforeNow
import com.mckernant1.lol.esports.api.models.Match
import com.mckernant1.lol.esports.api.svc.MatchService
import com.mckernant1.lol.esports.api.svc.TournamentService
import com.mckernant1.lol.esports.api.util.startDateAsDate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.temporal.ChronoUnit

@RestController
class MatchController(
    private val matchService: MatchService,
    private val tournamentService: TournamentService
) {

    @GetMapping("/matches/{tournamentId}")
    fun getMatchesForTournament(@PathVariable tournamentId: String): List<Match> {

        tournamentService.verifyTournamentExists(tournamentId)

        return matchService.getMatchesForTournament(tournamentId)
            .toList()

    }

    @GetMapping("/matches")
    fun getMatches(
        @RequestParam leagueId: String?,
        @RequestParam tournamentId: String?,
        @RequestParam(defaultValue = "200") limit: Int
    ): List<Match> {
        if (tournamentId != null) {
            tournamentService.verifyTournamentExists(tournamentId)
            return matchService.getMatchesForTournament(tournamentId).toList()
        }

        if (leagueId == null) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Must supply one of leagueId or tournamentId as query param"
            )
        }

        return tournamentService.getTournamentsForLeague(leagueId)
            .filter {
                it.startDateAsDate()
                    ?.minus(7, ChronoUnit.DAYS)
                    ?.isBeforeNow()
                    ?: false
            }.sortedByDescending {
                it.startDateAsDate()!!
            }.filter {
                !leagueId.equals("WCS", ignoreCase = true) || it.isOfficial.falseIfNull()
            }.flatMap {
                matchService.getMatchesForTournament(it.tournamentId)
            }.take(limit)
            .toList()
    }

}
