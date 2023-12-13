package com.authentication.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j(topic = "REQUEST-FILTER")
public class RequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("======> {}", request.getServletPath());

        String authorizeHeader = request.getHeader(AUTHORIZATION);
        if (authorizeHeader != null && authorizeHeader.startsWith("Bearer ")) {
            String jwtToken = authorizeHeader.substring("Bearer ".length());
            verifyToken(jwtToken, response);
            filterChain.doFilter(request, response);
        } else if (isAllow(request.getServletPath())) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(403);
            response.getOutputStream().print("{\n" +
                    "    \"timestamp\": \"" + new Date() + "\",\n" +
                    "    \"status\": 403,\n" +
                    "    \"path\": \"" + request.getServletPath() + "\",\n" +
                    "    \"error\": \"Unauthorized\",\n" +
                    "    \"messages\": \"Access denied\"\n" +
                    "}");
            response.setContentType(APPLICATION_JSON_VALUE);
        }
    }

    private boolean isAllow(String url) {
        List<String> whiteList = List.of(
                "/actuator",
                "/swagger-ui",
                "/v3/",
                "/auth/");
        return whiteList.stream().anyMatch(url::contains);
    }

    /**
     * Verify Token
     *
     * @param jwtToken
     * @param response
     * @throws IOException
     */
    public void verifyToken(String jwtToken, HttpServletResponse response) throws IOException {
        Algorithm algorithm = Algorithm.HMAC256("Api@Secret.Key".getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = verifier.verify(jwtToken);
            String username = decodedJWT.getSubject();
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("Admin"));
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (JWTVerificationException e) {
            log.error("Error logging in: {}", e.getMessage());
            response.setHeader("Error", e.getMessage());
            response.setStatus(FORBIDDEN.value());
            response.setContentType(APPLICATION_JSON_VALUE);

            Map<String, Object> errors = new HashMap<>();
            errors.put("status", FORBIDDEN.value());
            errors.put("message", e.getMessage());
            new ObjectMapper().writeValue(response.getOutputStream(), errors);
        }
    }
}