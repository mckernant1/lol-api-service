package com.mckernant1.lol.esports.api.util

import com.google.gson.Gson
import kotlin.reflect.KClass

fun <T : Any> Gson.mapToObject(map: Map<String, Any>, clazz: KClass<T>) : T {
    return this.fromJson(this.toJson(map), clazz.java)
}
