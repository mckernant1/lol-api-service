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
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class GraphQLController(
    private val leagueService: LeagueService,
    private val tournamentService: TournamentService,
    private val matchService: MatchService,
    private val playerService: PlayerService,
    private val teamService: TeamService
) {

    // LEAGUES

    @QueryMapping
    fun leagues(): List<League> = leagueService.scanLeagues().toList()

    @QueryMapping
    fun leagueById(@Argument leagueId: String): League? = leagueService.getLeague(leagueId)

    @SchemaMapping
    fun tournaments(league: League): List<Tournament> = tournamentService.getTournamentsForLeague(league.leagueId).toList()

    // TOURNAMENT

    @QueryMapping
    fun tournaments(): List<Tournament> = tournamentService.scanTournaments().toList()

    @QueryMapping
    fun tournamentById(@Argument tournamentId: String): Tournament? = tournamentService.getTournamentById(tournamentId)

    @SchemaMapping
    fun league(tournament: Tournament): League? = leagueService.getLeague(tournament.leagueId)

    @SchemaMapping
    fun matches(tournament: Tournament): List<Match> = matchService.getMatchesForTournament(tournament.tournamentId).toList()

    // MATCHES

    @QueryMapping
    fun matches(): List<Match> = matchService.scanMatches().toList()

    @SchemaMapping
    fun redTeam(match: Match): Team? = teamService.getTeam(match.redTeamId)

    @SchemaMapping
    fun blueTeam(match: Match): Team? = teamService.getTeam(match.blueTeamId)

    // PLAYERS

    @QueryMapping
    fun players(): List<Player> = playerService.scanPlayers().toList()

    @QueryMapping
    fun playerById(@Argument playerId: String): Player? = playerService.getPlayerById(playerId)

    @SchemaMapping
    fun team(player: Player): Team? = teamService.getTeam(player.teamId!!)

    // TEAMS

    @QueryMapping
    fun teams(): List<Team> = teamService.scanTeams().toList()

    @QueryMapping
    fun teamById(@Argument teamId: String): Team? = teamService.getTeam(teamId)

    @SchemaMapping
    fun players(team: Team): List<Player> = playerService.getPlayersOnTeam(team.teamId).toList()


}
