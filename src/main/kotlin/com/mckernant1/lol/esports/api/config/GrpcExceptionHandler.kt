package com.mckernant1.lol.esports.api.config

import com.mckernant1.commons.logging.Slf4j.logger
import com.mckernant1.lol.esports.api.config.GrpcExceptionConverter.toStatus
import io.grpc.Status

import org.slf4j.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.grpc.server.exception.GrpcExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


@Configuration
class GrpcExceptionHandler {


    companion object {
        private val logger: Logger = logger()
    }

    @Bean
    fun handleResponseStatusException(): GrpcExceptionHandler = GrpcExceptionHandler { e ->
        when {
            e is ResponseStatusException -> e.toStatus().asException()
            else -> {
                logger.error("Internal Error: ", e)
                Status.INTERNAL
                    .withCause(e)
                    .withDescription("An Internal Error has occurred")
                    .asException()
            }
        }
    }
}


object GrpcExceptionConverter {
    fun ResponseStatusException.toStatus(): Status = when (statusCode) {
        HttpStatus.NOT_FOUND -> Status.NOT_FOUND
        HttpStatus.BAD_REQUEST -> Status.INVALID_ARGUMENT
        HttpStatus.FORBIDDEN -> Status.PERMISSION_DENIED
        HttpStatus.UNAUTHORIZED -> Status.UNAUTHENTICATED
        else -> Status.UNKNOWN
    }
        .withCause(this)
        .withDescription(message)
}
