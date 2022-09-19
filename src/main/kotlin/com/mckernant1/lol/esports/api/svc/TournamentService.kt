package com.mckernant1.lol.esports.api.svc

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.QueryRequest
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.github.mckernant1.lol.esports.api.models.Tournament
import com.google.gson.Gson
import com.mckernant1.lol.esports.api.config.TOURNAMENTS_TABLE_NAME
import com.mckernant1.lol.esports.api.config.TOURNAMENT_INDEX
import com.mckernant1.lol.esports.api.util.mapToObject
import kotlin.jvm.Throws
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TournamentService(
    private val ddb: AmazonDynamoDB,
    private val gson: Gson
) {

    @Throws(ResponseStatusException::class)
    fun verifyTournamentExists(tournamentId: String): Unit {
        getTournamentById(tournamentId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "The tournament with id '$tournamentId' does not exist",
        )
    }

    fun scanTournaments(): Sequence<Tournament> = ddb.scan(ScanRequest(TOURNAMENTS_TABLE_NAME))
        .items
        .asSequence()
        .map { ItemUtils.toItem(it).asMap() }
        .map { gson.mapToObject(it, Tournament::class) }


    fun getTournamentsForLeague(leagueId: String): Sequence<Tournament> = ddb.query(
        QueryRequest(TOURNAMENTS_TABLE_NAME)
            .withKeyConditionExpression("leagueId = :desiredLeague")
            .withExpressionAttributeValues(
                mapOf(":desiredLeague" to AttributeValue(leagueId))
            )
    ).items.asSequence()
        .map { ItemUtils.toItem(it).asMap() }
        .map { gson.mapToObject(it, Tournament::class) }


    fun getTournamentById(tournamentId: String): Tournament? = ddb.query(
        QueryRequest(TOURNAMENTS_TABLE_NAME)
            .withIndexName(TOURNAMENT_INDEX)
            .withKeyConditionExpression("tournamentId = :desiredTourney")
            .withExpressionAttributeValues(
                mapOf(":desiredTourney" to AttributeValue(tournamentId))
            )
    ).items
        .asSequence()
        .map { ItemUtils.toItem(it).asMap() }
        .map { gson.mapToObject(it, Tournament::class) }
        .firstOrNull()


}
