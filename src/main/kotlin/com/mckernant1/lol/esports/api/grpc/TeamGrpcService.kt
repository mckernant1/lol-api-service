package com.mckernant1.lol.esports.api.grpc

import com.mckernant1.lol.esports.api.svc.TeamService
import com.mckernant1.lol.esports.api.util.toGrpc
import com.mckernant1.lol.teams.GetTeamRequest
import com.mckernant1.lol.teams.GetTeamResponse
import com.mckernant1.lol.teams.ListTeamsRequest
import com.mckernant1.lol.teams.ListTeamsResponse
import com.mckernant1.lol.teams.TeamServiceGrpcKt
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class TeamGrpcService(
    private val teamService: TeamService
) : TeamServiceGrpcKt.TeamServiceCoroutineImplBase() {


    override suspend fun listTeams(request: ListTeamsRequest): ListTeamsResponse {
        return ListTeamsResponse.newBuilder()
            .addAllTeams(teamService.scanTeams().map { it.toGrpc() }.toList())
            .build()
    }

    override suspend fun getTeam(request: GetTeamRequest): GetTeamResponse {

        teamService.assertTeamExists(request.teamId)

        return GetTeamResponse.newBuilder()
            .setTeam(teamService.getTeam(request.teamId)!!.toGrpc())
            .build()
    }

}
