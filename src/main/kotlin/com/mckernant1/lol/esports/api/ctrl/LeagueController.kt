package com.mckernant1.lol.esports.api.ctrl

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.github.mckernant1.lol.esports.api.League
import com.google.gson.Gson
import com.mckernant1.lol.esports.api.config.LEAGUES_TABLE_NAME
import com.mckernant1.lol.esports.api.util.mapToObject
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException


@RestController
class LeagueController(
    private val ddb: AmazonDynamoDB,
    private val gson: Gson
) {

    @GetMapping("/leagues")
    fun getAllLeagues(): List<League> {
        return ddb.scan(
            ScanRequest(LEAGUES_TABLE_NAME)
        ).items
            .asSequence()
            .map { ItemUtils.toItem(it).asMap() }
            .map { gson.mapToObject(it, League::class) }
            .toList()
    }


    @GetMapping("/leagues/{leagueId}")
    fun getLeague(@PathVariable leagueId: String): League {
        val item = ddb.getItem(
            LEAGUES_TABLE_NAME,
            mapOf("leagueId" to AttributeValue(leagueId))
        ).item ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "leagueId $leagueId does not exist")

        return gson.mapToObject(ItemUtils.toItem(item).asMap(), League::class)
    }

}
