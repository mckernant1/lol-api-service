package com.mckernant1.lol.esports.api.svc

import com.fasterxml.jackson.databind.ObjectMapper
import com.mckernant1.commons.extensions.convert.MapConverters.mapToObject
import com.mckernant1.lol.esports.api.models.Match
import com.mckernant1.lol.esports.api.config.MATCHES_TABLE_NAME
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Service
class MatchService(
    private val ddb: DynamoDbClient,
    private val objectMapper: ObjectMapper
) {

    fun getMatchesForTournament(tournamentId: String): Sequence<Match> = ddb.queryPaginator {
        it.tableName(MATCHES_TABLE_NAME)
        it.keyConditionExpression("tournamentId = :desiredTourney")
        it.expressionAttributeValues(
            mapOf(":desiredTourney" to AttributeValue.fromS(tournamentId))
        )
    }.items().asSequence()
        .filter { it.isNotEmpty() }
        .mapToObject(objectMapper)

    fun scanMatches(): Sequence<Match> = ddb.scanPaginator {
        it.tableName(MATCHES_TABLE_NAME)
    }.items().asSequence()
        .filter { it.isNotEmpty() }
        .mapToObject(objectMapper)


}
