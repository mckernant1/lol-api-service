package com.mckernant1.lol.esports.api.svc

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mckernant1.extensions.convert.MapConverters.mapToObject
import com.github.mckernant1.extensions.convert.MapConverters.toObject
import com.github.mckernant1.lol.esports.api.models.Player
import com.mckernant1.lol.esports.api.config.PLAYERS_TABLE_NAME
import com.mckernant1.lol.esports.api.config.PLAYERS_TABLE_TEAM_INDEX
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Service
class PlayerService(
    private val ddb: DynamoDbClient,
    private val objectMapper: ObjectMapper
) {
    fun getPlayersOnTeam(teamId: String): Sequence<Player> = ddb.queryPaginator {
        it.tableName(PLAYERS_TABLE_NAME)
        it.indexName(PLAYERS_TABLE_TEAM_INDEX)
        it.keyConditionExpression("teamId = :desiredTeam")
        it.expressionAttributeValues(
            mapOf(
                ":desiredTeam" to software.amazon.awssdk.services.dynamodb.model.AttributeValue.fromS(
                    teamId
                )
            )
        )
    }.items().asSequence()
        .filter { it.isNotEmpty() }
        .mapToObject(objectMapper)

    fun getPlayerById(playerId: String): Player? = ddb.getItem {
        it.tableName(PLAYERS_TABLE_NAME)
        it.key(mapOf("id" to AttributeValue.fromS(playerId)))
    }.item()?.toObject(objectMapper)


    fun scanPlayers(): Sequence<Player> = ddb.scanPaginator {
        it.tableName(PLAYERS_TABLE_NAME)
    }.items().asSequence()
        .filter { it.isNotEmpty() }
        .mapToObject(objectMapper)

}
