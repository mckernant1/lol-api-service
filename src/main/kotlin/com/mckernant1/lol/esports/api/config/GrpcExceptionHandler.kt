package com.mckernant1.lol.esports.api.config

import com.mckernant1.commons.logging.Slf4j.logger
import io.grpc.Status
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


@GrpcAdvice
class GrpcExceptionHandler {


    companion object {
        private val logger: Logger = logger()
    }

    @GrpcExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(e: ResponseStatusException): Status {
        return when (e.statusCode) {
            HttpStatus.NOT_FOUND -> Status.NOT_FOUND.withDescription(e.reason)
            else -> {
                logger.error("Internal Error: ", e)
                Status.INTERNAL.withCause(e).withDescription("An Internal Error has occurred")
            }
        }

    }


}
