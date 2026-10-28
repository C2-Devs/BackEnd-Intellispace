package com.intellispace.backend.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOG =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean diagnosticRequest =
                path.contains("/custom-furniture")
                || ("GET".equals(request.getMethod())
                    && path.matches("/api/workspaces/[^/]+"));

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            try {
                UUID userId = jwtService.validateAndGetUserId(header.substring(7));

                var authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, List.of());
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                var securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);

                if (diagnosticRequest) {
                    LOG.info(
                            "Auth diagnostic: JWT accepted, method={}, path={}",
                            request.getMethod(), path);
                }
            } catch (InvalidTokenException e) {
                if (diagnosticRequest) {
                    Throwable cause = e.getCause();
                    String reason = cause == null
                            ? e.getClass().getSimpleName()
                            : cause.getClass().getSimpleName();

                    LOG.warn(
                            "Auth diagnostic: JWT rejected, method={}, path={}, reason={}",
                            request.getMethod(), path, reason);
                }
            }
        } else if (diagnosticRequest) {
            LOG.warn(
                    "Auth diagnostic: missing or malformed bearer header, "
                            + "method={}, path={}, headerPresent={}",
                    request.getMethod(), path, header != null);
        }

        chain.doFilter(request, response);
    }
}
