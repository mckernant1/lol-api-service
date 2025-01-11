package com.mckernant1.lol.esports.api.util

import com.mckernant1.lol.esports.api.util.TimestampConverter.toTimestamp
import com.mckernant1.lol.leagues.League
import com.mckernant1.lol.matches.Match
import com.mckernant1.lol.players.Player
import com.mckernant1.lol.teams.Team
import com.mckernant1.lol.tournament.Tournament


private inline fun <T, V> T.doIfNotNull(value: V?, block: T.(V) -> Unit): T = apply {
    value?.let { block(it) }
}


fun com.mckernant1.lol.esports.api.models.League.toGrpc(): League = League.newBuilder()
    .setLeagueId(leagueId)
    .setIsOfficial(isOfficial)
    .setLeagueName(leagueName)
    .doIfNotNull(level) { setLevel(it) }
    .doIfNotNull(region) { setRegion(it) }
    .build()


fun com.mckernant1.lol.esports.api.models.Match.toGrpc(): Match = Match.newBuilder()
    .setTournamentId(tournamentId)
    .setMatchId(matchId)
    .doIfNotNull(bestOf) { setBestOf(it) }
    .setBlueTeamId(blueTeamId)
    .setRedTeamId(redTeamId)
    .doIfNotNull(patch) { setPatch(it) }
    .setStartTime(startTimeAsInstant().toTimestamp())
    .doIfNotNull(winner) { setWinner(it) }
    .build()


fun com.mckernant1.lol.esports.api.models.Player.toGrpc(): Player = Player.newBuilder()
    .setId(id)
    .doIfNotNull(age) { setAge(it) }
    .doIfNotNull(role) { setRole(it) }
    .doIfNotNull(country) { setCountry(it) }
    .doIfNotNull(isSubstitute) { setIsSubstitute(it) }
    .doIfNotNull(residency){ setResidency(it) }
    .doIfNotNull(teamId) { setTeamId(it) }
    .build()

fun com.mckernant1.lol.esports.api.models.Team.toGrpc(): Team = Team.newBuilder()
    .setTeamId(teamId)
    .setName(name)
    .doIfNotNull(location) { setLocation(it) }
    .doIfNotNull(isDisbanded) { setIsDisbanded(it) }
    .doIfNotNull(region) { setRegion(it) }
    .build()


fun com.mckernant1.lol.esports.api.models.Tournament.toGrpc(): Tournament = Tournament.newBuilder()
    .setLeagueId(leagueId)
    .setTournamentId(tournamentId)
    .doIfNotNull(startDate) { setStartTime(startDateAsDate()!!.toTimestamp()) }
    .doIfNotNull(endDate) { setEndTime(endDateAsDate()!!.toTimestamp()) }
    .doIfNotNull(isOfficial) { setIsOfficial(it) }
    .doIfNotNull(isPlayoffs) { setIsPlayoffs(it) }
    .doIfNotNull(isQualifier) { setIsQualifier(it) }
    .build()

