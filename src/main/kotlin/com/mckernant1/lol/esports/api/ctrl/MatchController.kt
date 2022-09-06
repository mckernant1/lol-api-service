package com.mckernant1.lol.esports.api.ctrl

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.QueryRequest
import com.github.mckernant1.lol.esports.api.Match
import com.google.gson.Gson
import com.mckernant1.lol.esports.api.config.MATCHES_TABLE_NAME
import com.mckernant1.lol.esports.api.config.TOURNAMENTS_TABLE_NAME
import com.mckernant1.lol.esports.api.config.TOURNAMENT_INDEX
import com.mckernant1.lol.esports.api.util.mapToObject
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class MatchController(
    private val ddb: AmazonDynamoDB,
    private val gson: Gson
) {

    @GetMapping("/matches/{tournamentId}")
    fun getMatchesForTournament(@PathVariable tournamentId: String): List<Match> {

        ddb.query(
            QueryRequest(TOURNAMENTS_TABLE_NAME)
                .withIndexName(TOURNAMENT_INDEX)
                .withKeyConditionExpression("tournamentId = :desiredTourney")
                .withExpressionAttributeValues(
                    mapOf(":desiredTourney" to AttributeValue(tournamentId))
                )
        ).items.ifEmpty {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "The tournament with id $tournamentId does not exist",
            )
        }

        return ddb.query(
            QueryRequest(MATCHES_TABLE_NAME)
                .withKeyConditionExpression("tournamentId = :desiredTourney")
                .withExpressionAttributeValues(
                    mapOf(":desiredTourney" to AttributeValue(tournamentId))
                )
        ).items
            .asSequence()
            .map { ItemUtils.toItem(it).asMap() }
            .map { gson.mapToObject(it, Match::class) }
            .toList()

    }


}
