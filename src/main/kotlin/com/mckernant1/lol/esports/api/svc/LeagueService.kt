package com.mckernant1.lol.esports.api.svc

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mckernant1.extensions.convert.MapConverters.mapToObject
import com.github.mckernant1.extensions.convert.MapConverters.toObject
import com.github.mckernant1.lol.esports.api.models.League
import com.mckernant1.lol.esports.api.config.LEAGUES_TABLE_NAME
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Service
class LeagueService(
    private val ddb: DynamoDbClient,
    private val objectMapper: ObjectMapper
) {

    @Throws(ResponseStatusException::class)
    fun assertLeagueExists(leagueId: String) {
        getLeague(leagueId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "leagueId '$leagueId' does not exist",
        )
    }

    fun scanLeagues(): Sequence<League> = ddb.scanPaginator {
        it.tableName(LEAGUES_TABLE_NAME)
    }.items().asSequence().mapToObject(objectMapper)


    fun getLeague(leagueId: String): League? {
        val item = ddb.getItem {
            it.tableName(LEAGUES_TABLE_NAME)
            it.key(mapOf("leagueId" to AttributeValue.fromS(leagueId)))
        }.item() ?: return null

        return item.toObject(objectMapper)
    }

}
