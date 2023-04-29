package com.mckernant1.lol.esports.api.ctrl

import com.mckernant1.lol.esports.api.models.Match
import com.mckernant1.lol.esports.api.svc.MatchService
import com.mckernant1.lol.esports.api.svc.TournamentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

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

}
