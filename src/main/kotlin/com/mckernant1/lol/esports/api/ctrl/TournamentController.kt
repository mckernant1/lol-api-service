package com.mckernant1.lol.esports.api.ctrl

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.QueryRequest
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.github.mckernant1.extensions.boolean.falseIfNull
import com.github.mckernant1.lol.esports.api.models.Tournament
import com.google.gson.Gson
import com.mckernant1.lol.esports.api.config.LEAGUES_TABLE_NAME
import com.mckernant1.lol.esports.api.config.TOURNAMENTS_TABLE_NAME
import com.mckernant1.lol.esports.api.config.TOURNAMENT_INDEX
import com.mckernant1.lol.esports.api.util.endDateAsDate
import com.mckernant1.lol.esports.api.util.mapToObject
import com.mckernant1.lol.esports.api.util.startDateAsDate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
class TournamentController(
    private val ddb: AmazonDynamoDB,
    private val gson: Gson
) {

    @GetMapping("/ongoing-tournaments")
    fun getOngoingTournaments(): List<Tournament> {
        return ddb.scan(ScanRequest(TOURNAMENTS_TABLE_NAME))
            .items
            .asSequence()
            .map { ItemUtils.toItem(it).asMap() }
            .map { gson.mapToObject(it, Tournament::class) }
            .filter {
                it.startDateAsDate()?.isBefore(Instant.now()).falseIfNull()
                        && it.endDateAsDate()?.isAfter(Instant.now()).falseIfNull()
            }.toList()
    }


    @GetMapping("/tournament/{tournamentId}")
    fun getTournament(@PathVariable tournamentId: String): Tournament {
        return ddb.query(
            QueryRequest(TOURNAMENTS_TABLE_NAME)
                .withIndexName(TOURNAMENT_INDEX)
                .withKeyConditionExpression("tournamentId = :desiredTourney")
                .withExpressionAttributeValues(
                    mapOf(":desiredTourney" to AttributeValue(tournamentId))
                )
        )
            .items
            .asSequence()
            .map { ItemUtils.toItem(it).asMap() }
            .map { gson.mapToObject(it, Tournament::class) }
            .firstOrNull()
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "The tournament with id $tournamentId does not exist"
            )
    }

    @GetMapping("/tournaments/{leagueId}")
    fun getTournamentForLeague(@PathVariable leagueId: String): List<Tournament> {

        ddb.getItem(
            LEAGUES_TABLE_NAME,
            mapOf("leagueId" to AttributeValue(leagueId))
        ).item ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "leagueId $leagueId does not exist",
        )

        return ddb.query(
            QueryRequest(TOURNAMENTS_TABLE_NAME)
                .withKeyConditionExpression("leagueId = :desiredLeague")
                .withExpressionAttributeValues(
                    mapOf(":desiredLeague" to AttributeValue(leagueId))
                )
        ).items.asSequence()
            .map { ItemUtils.toItem(it).asMap() }
            .map { gson.mapToObject(it, Tournament::class) }
            .toList()

    }

}
