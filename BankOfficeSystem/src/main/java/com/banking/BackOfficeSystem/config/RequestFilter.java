package com.banking.BackOfficeSystem.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestFilter implements Filter {

    private static final String BACKOFFICE_URL = "http://localhost:8002";
    private static final String ONLINEBANKING_URL = "http://localhost:8001";
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(BACKOFFICE_URL, ONLINEBANKING_URL);
    
    private static final String ALLOWED_METHODS = "POST, PUT, GET, OPTIONS, DELETE";
    private static final String ALLOWED_HEADERS = "Authorization, Content-Type, Accept, Origin, X-Requested-With";
    private static final String MAX_AGE = "3600";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");
        if (ALLOWED_ORIGINS.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        // Set CORS headers
        setCorsHeaders(response);

        // Handle pre-flight requests
        if (isPreFlightRequest(request)) {
            handlePreFlight(response);
            return;
        }

        try {
            chain.doFilter(req, res);
        } catch (Exception e) {
            log.error("Error processing request: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
        response.setHeader("Access-Control-Allow-Headers", ALLOWED_HEADERS);
        response.setHeader("Access-Control-Max-Age", MAX_AGE);
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    private boolean isPreFlightRequest(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    private void handlePreFlight(HttpServletResponse response) {
        log.debug("Handling pre-flight request");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("Initializing CORS filter");
    }

    @Override
    public void destroy() {
        log.info("Destroying CORS filter");
    }
}