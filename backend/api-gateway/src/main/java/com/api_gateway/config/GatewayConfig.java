package com.api_gateway.config;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Sau khi Spring Security xác thực JWT, filter này đọc subject (Keycloak user ID)
 * từ SecurityContext và chèn vào header X-User-Id trước khi gateway
 * forward request xuống các microservice downstream.
 */
@Component
public class GatewayConfig extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtToken) {
            String userId = jwtToken.getToken().getSubject();
            filterChain.doFilter(new UserIdRequestWrapper(request, userId), response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    // ── Wrapper adds X-User-Id without mutating the original request ──────────

    private static class UserIdRequestWrapper extends HttpServletRequestWrapper {

        private final String userId;

        UserIdRequestWrapper(HttpServletRequest request, String userId) {
            super(request);
            this.userId = userId;
        }

        @Override
        public String getHeader(String name) {
            if (USER_ID_HEADER.equalsIgnoreCase(name)) return userId;
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (USER_ID_HEADER.equalsIgnoreCase(name)) {
                return Collections.enumeration(List.of(userId));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            if (names.stream().noneMatch(USER_ID_HEADER::equalsIgnoreCase)) {
                names.add(USER_ID_HEADER);
            }
            return Collections.enumeration(names);
        }
    }
}


