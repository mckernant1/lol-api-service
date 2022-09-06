package com.mckernant1.lol.esports.api.ctrl

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.QueryRequest
import com.github.mckernant1.lol.esports.api.Player
import com.google.gson.Gson
import com.mckernant1.lol.esports.api.config.PLAYERS_TABLE_NAME
import com.mckernant1.lol.esports.api.config.PLAYERS_TABLE_TEAM_INDEX
import com.mckernant1.lol.esports.api.config.TEAMS_TABLE_NAME
import com.mckernant1.lol.esports.api.util.mapToObject
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class PlayerController(
    private val ddb: AmazonDynamoDB,
    private val gson: Gson
) {

    @GetMapping("/players/{teamId}")
    fun getPlayersOnTeam(@PathVariable teamId: String): List<Player> {
        ddb.getItem(
            TEAMS_TABLE_NAME,
            mapOf(
                "teamId" to AttributeValue(teamId)
            )
        ).item ?: throw ResponseStatusException( HttpStatus.NOT_FOUND,
            "teamId $teamId does not exist",
        )

        return ddb.query(
            QueryRequest(PLAYERS_TABLE_NAME)
                .withIndexName(PLAYERS_TABLE_TEAM_INDEX)
                .withKeyConditionExpression("teamId = :desiredTeam")
                .withExpressionAttributeValues(mapOf(":desiredTeam" to AttributeValue(teamId)))
        ).items.asSequence()
            .map { ItemUtils.toItem(it).asMap() }
            .map { gson.mapToObject(it, Player::class) }
            .toList()
    }

}
