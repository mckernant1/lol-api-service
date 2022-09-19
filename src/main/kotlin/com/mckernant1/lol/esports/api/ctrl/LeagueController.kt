package com.mckernant1.lol.esports.api.ctrl

import com.github.mckernant1.lol.esports.api.models.League
import com.mckernant1.lol.esports.api.svc.LeagueService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class LeagueController(
    private val leagueService: LeagueService
) {

    @GetMapping("/leagues")
    fun getAllLeagues(): List<League> {
        return leagueService.scanLeagues().toList()
    }


    @GetMapping("/leagues/{leagueId}")
    fun getLeague(@PathVariable leagueId: String): League {

        leagueService.assertLeagueExists(leagueId)

        return leagueService.getLeague(leagueId)!!
    }

}
