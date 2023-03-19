package com.trevorism.gcloud.webapi

import io.micronaut.http.HttpRequest
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.HttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Filter("/api/**")
class LoggingRequestFilter implements HttpServerFilter {
    private static final Logger log = LoggerFactory.getLogger(LoggingRequestFilter)

    @Override
    Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        String correlationId = request.getHeaders().get("X-Correlation-ID")
        if (correlationId != null) {
            log.info("CorrelationId: ${correlationId}")
        }
        chain.proceed(request)
    }

    @Override
    int getOrder() {
        return 0
    }
}
