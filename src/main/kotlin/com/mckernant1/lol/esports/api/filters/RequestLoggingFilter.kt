package com.mckernant1.lol.esports.api.filters

import com.github.mckernant1.standalone.measureDuration
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
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestId = UUID.randomUUID().toString()
        MDC.put("RequestId", requestId)

        logger.info("START")
        response.addHeader("X-Request-Id", requestId)

        val timeTaken = measureDuration {
            filterChain.doFilter(request, response)
        }

        logger.info("RequestURI: '${request.requestURI}' Method: '${request.method}' ResponseStatus: '${response.status}' RequestDuration: '${timeTaken.toMillis()}ms'")
        logger.info("END")

        MDC.remove("RequestId")
    }
}
