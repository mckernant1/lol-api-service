package com.mckernant1.lol.esports.api.svc

import com.github.mckernant1.lol.esports.api.models.Player
import com.mckernant1.lol.esports.api.config.PLAYERS_TABLE_NAME
import com.mckernant1.lol.esports.api.config.PLAYERS_TABLE_TEAM_INDEX
import com.mckernant1.lol.esports.api.util.mapToObject
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Service
class PlayerService(
    private val ddb: DynamoDbClient
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
    }.items().asSequence().mapToObject()


    fun scanPlayers(): Sequence<Player> = ddb.scanPaginator {
        it.tableName(PLAYERS_TABLE_NAME)
    }.items().asSequence().mapToObject()

}
