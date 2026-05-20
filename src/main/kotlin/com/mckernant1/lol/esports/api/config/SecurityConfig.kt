package com.mckernant1.lol.esports.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                    .requestMatchers(HttpMethod.POST, "/graphql").permitAll()
                    .requestMatchers(HttpMethod.GET, "/graphiql", "/graphiql/**").permitAll()
                    .requestMatchers(HttpMethod.GET,
                        "/leagues",
                        "/leagues/{leagueId}",
                        "/teams",
                        "/teams/{teamId}",
                        "/matches",
                        "/matches/{tournamentId}",
                        "/players/{teamId}",
                        "/ongoing-tournaments",
                        "/tournament/{tournamentId}",
                        "/tournaments/{leagueId}",
                        "/most-recent-tournament/{leagueId}",
                    ).permitAll()
                    .anyRequest().denyAll()
            }

        return http.build()
    }
}
