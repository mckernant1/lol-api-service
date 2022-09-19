package com.mckernant1.lol.esports.api.svc

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.github.mckernant1.lol.esports.api.models.League
import com.google.gson.Gson
import com.mckernant1.lol.esports.api.config.LEAGUES_TABLE_NAME
import com.mckernant1.lol.esports.api.util.mapToObject
import kotlin.jvm.Throws
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class LeagueService(
    private val ddb: AmazonDynamoDB,
    private val gson: Gson,
) {

    @Throws(ResponseStatusException::class)
    fun assertLeagueExists(leagueId: String) {
        ddb.getItem(LEAGUES_TABLE_NAME, mapOf("leagueId" to AttributeValue(leagueId))).item
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "leagueId '$leagueId' does not exist",
            )
    }

    fun scanLeagues(): Sequence<League> = ddb.scan(
        ScanRequest(LEAGUES_TABLE_NAME)
    ).items
        .asSequence()
        .map { ItemUtils.toItem(it).asMap() }
        .map { gson.mapToObject(it, League::class) }


    fun getLeague(leagueId: String): League? {
        val item = ddb.getItem(
            LEAGUES_TABLE_NAME,
            mapOf("leagueId" to AttributeValue(leagueId))
        ).item ?: return null

        return gson.mapToObject(ItemUtils.toItem(item).asMap(), League::class)
    }

}
