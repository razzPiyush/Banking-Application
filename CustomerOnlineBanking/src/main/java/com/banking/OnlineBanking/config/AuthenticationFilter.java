package com.banking.OnlineBanking.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
The AuthenticationFilter.java file defines a custom authentication filter for Spring Security setup. Its main role is to:

- Intercept HTTP requests that require authentication.
- Extract the JWT or Bearer token from the Authorization header.
- Validate and authenticate the token using Spring Security's authentication manager.
- Set the authentication in the security context if successful, allowing the request to proceed.
- Reject requests with missing or invalid tokens.

In summary, this filter ensures that only requests with valid authentication tokens can access protected resources in your banking application.
*/
@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String MISSING_TOKEN_MESSAGE = "Authorization token is missing";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid authorization token format";

    public AuthenticationFilter(final RequestMatcher requiresAuth) {
        super(requiresAuth);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, 
            HttpServletResponse response) throws AuthenticationException {
        
        try {
            String token = extractToken(request);
            return authenticateToken(token);
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw new AuthenticationException(e.getMessage()) {};
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain chain, 
            Authentication authResult) throws IOException, ServletException {
        
        try {
            SecurityContextHolder.getContext().setAuthentication(authResult);
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in authentication chain: {}", e.getMessage());
            throw e;
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.isBlank(authHeader)) {
            log.warn("Missing authorization header");
            throw new IllegalArgumentException(MISSING_TOKEN_MESSAGE);
        }

        if (!authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Invalid authorization header format");
            throw new IllegalArgumentException(INVALID_TOKEN_MESSAGE);
        }

        return authHeader.substring(BEARER_PREFIX.length()).trim();
    }

    private Authentication authenticateToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException(INVALID_TOKEN_MESSAGE);
        }

        Authentication requestAuthentication = new UsernamePasswordAuthenticationToken(token, token);
        return getAuthenticationManager().authenticate(requestAuthentication);
    }
}