package com.banking.OnlineBanking.config;

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

/*
The RequestFilter.java file defines a custom servlet filter for handling CORS (Cross-Origin Resource Sharing) in application.
Its main role is to:

- Allow HTTP requests from specific origins (e.g., your banking frontends).
- Set CORS headers to enable cross-origin requests from allowed domains.
- Handle pre-flight OPTIONS requests required by browsers for CORS.
- Restrict HTTP methods and headers to those specified.
- Log allowed origins and filter lifecycle events.
- Return appropriate responses for pre-flight and error scenarios.

In summary, this filter ensures that only requests from trusted origins can interact with your APIs, enabling secure cross-origin communication
for banking application.
*/
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestFilter implements Filter {

    private static final String BANKOFFICE_URL = "http://localhost:8000";
    private static final String ONLINEBANKING_URL = "http://localhost:8001";
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(BANKOFFICE_URL, ONLINEBANKING_URL);
    
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
            log.debug("Allowed origin: {}", origin);
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