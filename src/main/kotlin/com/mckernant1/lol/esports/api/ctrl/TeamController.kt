package com.mckernant1.lol.esports.api.ctrl

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.github.mckernant1.lol.esports.api.models.Team
import com.google.gson.Gson
import com.mckernant1.lol.esports.api.config.TEAMS_TABLE_NAME
import com.mckernant1.lol.esports.api.util.mapToObject
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class TeamController(
    private val ddb: AmazonDynamoDB,
    private val gson: Gson
) {

    @GetMapping("/teams")
    fun getAllTeams(): List<Team> {
        return ddb.scan(
            ScanRequest(TEAMS_TABLE_NAME)
        ).items.asSequence()
            .map { ItemUtils.toItem(it).asMap() }
            .map { gson.mapToObject(it, Team::class) }
            .toList()
    }

    @GetMapping("/teams/{teamId}")
    fun getTeam(@PathVariable teamId: String): Team {

        val item = ddb.getItem(
            TEAMS_TABLE_NAME,
            mapOf(
                "teamId" to AttributeValue(teamId)
            )
        ).item ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "teamId $teamId does not exist",
        )

        return gson.mapToObject(ItemUtils.toItem(item).asMap(), Team::class)

    }

}
