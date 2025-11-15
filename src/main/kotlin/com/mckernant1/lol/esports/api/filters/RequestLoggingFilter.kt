package com.mckernant1.lol.esports.api.filters


import com.mckernant1.commons.standalone.measureDuration
import jakarta.servlet.FilterChain
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "RequestLoggingFilter", urlPatterns = ["/*"])
class RequestLoggingFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestId = UUID.randomUUID().toString()
        MDC.put("RequestId", requestId)

        logger.info("START")
        response.addHeader("X-Request-Id", requestId)
        if (
            request.requestURI.matches("[\\w/]+".toRegex()) &&
            request.method.matches("\\w+".toRegex())
        ) {
            logger.info("Start - RequestURI: '${request.requestURI}' Method: '${request.method}'")
        } else {
            logger.warn("Start - Request had unfamiliar characters... Skipping logging")
        }

        val timeTaken = measureDuration {
            filterChain.doFilter(request, response)
        }

        if (
            request.requestURI.matches("[\\w/]+".toRegex()) &&
            request.method.matches("\\w+".toRegex())
        ) {
            logger.info("End - RequestURI: '${request.requestURI}' Method: '${request.method}' ResponseStatus: '${response.status}' RequestDuration: '${timeTaken.toMillis()}ms'")
        } else {
            logger.warn("End - Request had unfamiliar characters... Skipping logging")
        }
        logger.info("END")

        MDC.remove("RequestId")
    }
}
