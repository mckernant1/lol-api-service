package com.mckernant1.lol.esports.api.svc

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mckernant1.extensions.convert.MapConverters.mapToObject
import com.github.mckernant1.extensions.convert.MapConverters.toObject
import com.github.mckernant1.lol.esports.api.models.Team
import com.mckernant1.lol.esports.api.config.TEAMS_TABLE_NAME
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Service
class TeamService(
    private val ddb: DynamoDbClient,
    private val objectMapper: ObjectMapper
) {

    @Throws(ResponseStatusException::class)
    fun assertTeamExists(teamId: String) {
        getTeam(teamId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "teamId '$teamId' does not exist",
        )
    }

    fun getTeam(teamId: String): Team? {
        val item = ddb.getItem {
            it.tableName(TEAMS_TABLE_NAME)
            it.key(mapOf("teamId" to AttributeValue.fromS(teamId)))
        }.item() ?: return null

        return item.toObject(objectMapper)
    }

    fun scanTeams(): Sequence<Team> = ddb.scanPaginator {
        it.tableName(TEAMS_TABLE_NAME)
    }.items().asSequence().mapToObject(objectMapper)

}
