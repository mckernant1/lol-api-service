package com.mckernant1.lol.esports.api.svc

import com.fasterxml.jackson.databind.ObjectMapper
import com.mckernant1.commons.extensions.convert.MapConverters.mapToObject
import com.mckernant1.lol.esports.api.config.TOURNAMENTS_TABLE_NAME
import com.mckernant1.lol.esports.api.config.TOURNAMENT_INDEX
import com.mckernant1.lol.esports.api.models.Tournament
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Service
class TournamentService(
    private val ddb: DynamoDbClient,
    private val objectMapper: ObjectMapper
) {

    @Throws(ResponseStatusException::class)
    fun verifyTournamentExists(tournamentId: String): Unit {
        getTournamentById(tournamentId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "The tournament with id '$tournamentId' does not exist",
        )
    }

    fun scanTournaments(): Sequence<Tournament> = ddb.scanPaginator {
        it.tableName(TOURNAMENTS_TABLE_NAME)
    }.items().asSequence()
        .filter { it.isNotEmpty() }
        .mapToObject(objectMapper)


    fun getTournamentsForLeague(leagueId: String): Sequence<Tournament> = ddb.queryPaginator {
        it.tableName(TOURNAMENTS_TABLE_NAME)
        it.keyConditionExpression("leagueId = :desiredLeague")
        it.expressionAttributeValues(
            mapOf(":desiredLeague" to AttributeValue.fromS(leagueId))
        )
    }.items().asSequence()
        .filter { it.isNotEmpty() }
        .mapToObject(objectMapper)


    fun getTournamentById(tournamentId: String): Tournament? = ddb.queryPaginator {
        it.tableName(TOURNAMENTS_TABLE_NAME)
        it.indexName(TOURNAMENT_INDEX)
        it.keyConditionExpression("tournamentId = :desiredTourney")
        it.expressionAttributeValues(
            mapOf(":desiredTourney" to AttributeValue.fromS(tournamentId))
        )
    }.items()
        .asSequence()
        .filter { it.isNotEmpty() }
        .mapToObject<Tournament>(objectMapper)
        .firstOrNull()


}
