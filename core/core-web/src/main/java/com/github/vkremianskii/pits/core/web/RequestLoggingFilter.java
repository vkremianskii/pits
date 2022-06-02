package com.github.vkremianskii.pits.core.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import static com.github.vkremianskii.pits.core.log.LogUtils.withMDCOnTerminate;
import static java.util.UUID.randomUUID;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Component
@Order(HIGHEST_PRECEDENCE)
public class RequestLoggingFilter implements WebFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
            .doOnEach(withMDCOnTerminate(() -> {
                final var request = exchange.getRequest();
                final var response = exchange.getResponse();
                LOG.info("Request: " + request.getMethod() + " " + request.getURI() + " -> " + response.getStatusCode());
                LOG.debug("Headers: " + request.getHeaders());
            }))
            .contextWrite(Context.of("requestId", randomUUID()));
    }
}
