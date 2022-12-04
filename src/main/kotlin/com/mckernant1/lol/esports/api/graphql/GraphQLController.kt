package com.mckernant1.lol.esports.api.graphql

import com.github.mckernant1.lol.esports.api.models.League
import com.github.mckernant1.lol.esports.api.models.Match
import com.github.mckernant1.lol.esports.api.models.Player
import com.github.mckernant1.lol.esports.api.models.Team
import com.github.mckernant1.lol.esports.api.models.Tournament
import com.mckernant1.lol.esports.api.svc.LeagueService
import com.mckernant1.lol.esports.api.svc.MatchService
import com.mckernant1.lol.esports.api.svc.PlayerService
import com.mckernant1.lol.esports.api.svc.TeamService
import com.mckernant1.lol.esports.api.svc.TournamentService
import org.springframework.graphql.data.method.annotation.QueryMapping

//@Controller
//@RequestMapping("/graphql")
class GraphQLController(
    val leagueService: LeagueService,
    val tournamentService: TournamentService,
    val matchService: MatchService,
    val playerService: PlayerService,
    val teamService: TeamService
) {


    @QueryMapping
    fun leagues(): List<League> = leagueService.scanLeagues().toList()

    @QueryMapping
    fun tournaments(): List<Tournament> = tournamentService.scanTournaments().toList()

    @QueryMapping
    fun matches(): List<Match> = matchService.scanMatches().toList()

    @QueryMapping
    fun players(): List<Player> = playerService.scanPlayers().toList()

    @QueryMapping
    fun teamService(): List<Team> = teamService.scanTeams().toList()


}
