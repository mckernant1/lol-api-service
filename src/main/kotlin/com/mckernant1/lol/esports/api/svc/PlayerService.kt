package com.mckernant1.lol.esports.api.svc

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.QueryRequest
import com.github.mckernant1.lol.esports.api.models.Player
import com.google.gson.Gson
import com.mckernant1.lol.esports.api.config.PLAYERS_TABLE_NAME
import com.mckernant1.lol.esports.api.config.PLAYERS_TABLE_TEAM_INDEX
import com.mckernant1.lol.esports.api.util.mapToObject
import org.springframework.stereotype.Service

@Service
class PlayerService(
    private val ddb: AmazonDynamoDB,
    private val gson: Gson
) {


    fun getPlayersOnTeam(teamId: String): Sequence<Player> = ddb.query(
        QueryRequest(PLAYERS_TABLE_NAME)
            .withIndexName(PLAYERS_TABLE_TEAM_INDEX)
            .withKeyConditionExpression("teamId = :desiredTeam")
            .withExpressionAttributeValues(mapOf(":desiredTeam" to AttributeValue(teamId)))
    ).items.asSequence()
        .map { ItemUtils.toItem(it).asMap() }
        .map { gson.mapToObject(it, Player::class) }


}
