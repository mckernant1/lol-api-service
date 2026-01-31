package com.mckernant1.lol.esports.api.filters


import com.mckernant1.commons.standalone.Measure.measureDuration
import jakarta.servlet.FilterChain
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "RequestLoggingFilter", urlPatterns = ["/*"])
class RequestLoggingFilter : OncePerRequestFilter() {

    companion object {
        private val requestURIRegex = "[\\w/\\s+.-?]+".toRegex()
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestId = UUID.randomUUID().toString()
        MDC.put("RequestId", requestId)

        response.addHeader("X-Request-Id", requestId)
        if (request.requestURI.matches(requestURIRegex)) {
            logger.info("Start - RequestURI: '${request.requestURI}' Method: '${request.method}'")
        } else {
            logger.warn("Start - Request had unfamiliar characters... Skipping logging")
        }

        val timeTaken = measureDuration {
            filterChain.doFilter(request, response)
        }

        if (request.requestURI.matches(requestURIRegex)) {
            logger.info("End - RequestURI: '${request.requestURI}' Method: '${request.method}' ResponseStatus: '${response.status}' RequestDuration: '${timeTaken.toMillis()}ms'")
        } else {
            logger.warn("End - Request had unfamiliar characters... Skipping logging. Status: '${response.status}' RequestDuration: '${timeTaken.toMillis()}ms'")
        }

        MDC.remove("RequestId")
    }
}
