package com.mckernant1.lol.esports.api.svc

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.github.mckernant1.lol.esports.api.models.Team
import com.google.gson.Gson
import com.mckernant1.lol.esports.api.config.TEAMS_TABLE_NAME
import com.mckernant1.lol.esports.api.util.mapToObject
import kotlin.jvm.Throws
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TeamService(
    private val ddb: AmazonDynamoDB,
    private val gson: Gson
) {

    @Throws(ResponseStatusException::class)
    fun assertTeamExists(teamId: String) {
        ddb.getItem(TEAMS_TABLE_NAME, mapOf("teamId" to AttributeValue(teamId))).item
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "teamId '$teamId' does not exist",
            )
    }

    fun getTeam(teamId: String): Team? {
        val item = ddb.getItem(
            TEAMS_TABLE_NAME,
            mapOf(
                "teamId" to AttributeValue(teamId)
            )
        ).item ?: return null

        return gson.mapToObject(ItemUtils.toItem(item).asMap(), Team::class)
    }

    fun scanTeams(): Sequence<Team> = ddb.scan(
        ScanRequest(TEAMS_TABLE_NAME)
    ).items.asSequence()
        .map { ItemUtils.toItem(it).asMap() }
        .map { gson.mapToObject(it, Team::class) }

}
