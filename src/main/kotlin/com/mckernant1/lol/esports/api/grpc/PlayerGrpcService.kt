package com.mckernant1.lol.esports.api.grpc

import com.mckernant1.lol.esports.api.svc.PlayerService
import com.mckernant1.lol.esports.api.svc.TeamService
import com.mckernant1.lol.esports.api.util.toGrpc
import com.mckernant1.lol.players.GetPlayersOnTeamRequest
import com.mckernant1.lol.players.GetPlayersOnTeamResponse
import com.mckernant1.lol.players.PlayerServiceGrpcKt
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class PlayerGrpcService(
    private val teamService: TeamService,
    private val playerService: PlayerService
) : PlayerServiceGrpcKt.PlayerServiceCoroutineImplBase() {

    override suspend fun getPlayersOnTeam(request: GetPlayersOnTeamRequest): GetPlayersOnTeamResponse {
        teamService.assertTeamExists(request.teamId)

        return GetPlayersOnTeamResponse.newBuilder()
            .addAllPlayers(
                playerService.getPlayersOnTeam(request.teamId)
                    .map { it.toGrpc() }
                    .toList()
            )
            .build()
    }

}
