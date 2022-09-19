package com.mckernant1.lol.esports.api.svc

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.QueryRequest
import com.github.mckernant1.lol.esports.api.models.Match
import com.google.gson.Gson
import com.mckernant1.lol.esports.api.config.MATCHES_TABLE_NAME
import com.mckernant1.lol.esports.api.util.mapToObject
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val ddb: AmazonDynamoDB,
    private val gson: Gson
) {

    fun getMatchesForTournament(tournamentId: String) = ddb.query(
        QueryRequest(MATCHES_TABLE_NAME)
            .withKeyConditionExpression("tournamentId = :desiredTourney")
            .withExpressionAttributeValues(
                mapOf(":desiredTourney" to AttributeValue(tournamentId))
            )
    ).items
        .asSequence()
        .map { ItemUtils.toItem(it).asMap() }
        .map { gson.mapToObject(it, Match::class) }

}
