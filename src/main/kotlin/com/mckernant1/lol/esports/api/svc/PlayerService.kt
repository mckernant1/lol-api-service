package com.mckernant1.lol.esports.api.svc

import com.fasterxml.jackson.databind.ObjectMapper
import com.mckernant1.commons.extensions.convert.MapConverters.mapToObject
import com.mckernant1.commons.extensions.convert.MapConverters.toObject
import com.mckernant1.lol.esports.api.config.PLAYERS_ID_INDEX
import com.mckernant1.lol.esports.api.models.Player
import com.mckernant1.lol.esports.api.config.PLAYERS_TABLE_NAME
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
        it.keyConditionExpression("teamId = :desiredTeam")
        it.expressionAttributeValues(
            mapOf(
                ":desiredTeam" to AttributeValue.fromS(
                    teamId
                )
            )
        )
    }.items().asSequence()
        .filter { it.isNotEmpty() }
        .mapToObject(objectMapper)

    
    fun getPlayerById(teamId: String, playerId: String): Player? = ddb.getItem {
        it.tableName(PLAYERS_TABLE_NAME)
        it.key(
            mapOf(
                "teamId" to AttributeValue.fromS(teamId),
                "id" to AttributeValue.fromS(playerId)
            )
        )
    }.item()
        ?.toObject(objectMapper)

    /**
     * Players can have the same id, but different teamId
     */
    fun getPlayersById(playerId: String): Sequence<Player> = ddb.queryPaginator {
        it.tableName(PLAYERS_TABLE_NAME)
        it.indexName(PLAYERS_ID_INDEX)
        it.keyConditionExpression("id = :desiredId")
        it.expressionAttributeValues(
            mapOf(":desiredId" to AttributeValue.fromS(playerId))
        )
    }.items()
        .asSequence()
        .filter { it.isNotEmpty() }
        .mapToObject(objectMapper)


    fun scanPlayers(): Sequence<Player> = ddb.scanPaginator {
        it.tableName(PLAYERS_TABLE_NAME)
    }.items().asSequence()
        .filter { it.isNotEmpty() }
        .mapToObject(objectMapper)

}
