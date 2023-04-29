package com.mckernant1.lol.esports.api.ctrl

import com.mckernant1.lol.esports.api.models.Team
import com.mckernant1.lol.esports.api.svc.TeamService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class TeamController(
    private val teamService: TeamService
) {

    @GetMapping("/teams")
    fun getAllTeams(): List<Team> {
        return teamService.scanTeams()
            .toList()
    }

    @GetMapping("/teams/{teamId}")
    fun getTeam(@PathVariable teamId: String): Team {

        teamService.assertTeamExists(teamId)

        return teamService.getTeam(teamId)!!
    }

}
