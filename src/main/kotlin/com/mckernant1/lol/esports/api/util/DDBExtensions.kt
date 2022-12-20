package com.mckernant1.lol.esports.api.util

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse

fun GetItemResponse.itemOrNull(): MutableMap<String, AttributeValue>? =
    if (item().isEmpty()) null else item()
