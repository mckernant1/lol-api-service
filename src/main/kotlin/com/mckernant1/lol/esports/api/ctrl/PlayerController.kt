package com.mckernant1.lol.esports.api.ctrl

import com.github.mckernant1.lol.esports.api.models.Player
import com.mckernant1.lol.esports.api.svc.PlayerService
import com.mckernant1.lol.esports.api.svc.TeamService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class PlayerController(
    private val teamService: TeamService,
    private val playerService: PlayerService
) {

    @GetMapping("/players/{teamId}")
    fun getPlayersOnTeam(@PathVariable teamId: String): List<Player> {
        teamService.assertTeamExists(teamId)

        return playerService.getPlayersOnTeam(teamId)
            .toList()
    }

}
