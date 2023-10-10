package com.mckernant1.lol.esports.api.ctrl

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.mckernant1.lol.esports.api.models.League
import com.mckernant1.lol.esports.api.svc.LeagueService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.Duration


@RestController
class LeagueController(
    private val leagueService: LeagueService
) {

    @GetMapping("/leagues")
    fun getAllLeagues(): List<League> {
        return leagueService.scanLeagues().toList()
    }

    private val leagueCache: LoadingCache<String, League> = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofMinutes(30))
        .expireAfterWrite(Duration.ofHours(12))
        .build(CacheLoader.from { leagueId ->
            leagueService.getLeague(leagueId)!!
        })

    @GetMapping("/leagues/{leagueId}")
    fun getLeague(@PathVariable leagueId: String): League {

        leagueService.assertLeagueExists(leagueId)

        return leagueCache[leagueId]
    }

}
