package com.mono.config;

import com.mono.exception.InvalidDataException;
import com.mono.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class RequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Request URL: {}", request.getServletPath());

        if (permitUrl(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizeHeader = request.getHeader(AUTHORIZATION);
        if (authorizeHeader != null && authorizeHeader.startsWith("Bearer ")) {
            String jwtToken = authorizeHeader.substring("Bearer ".length());
            TokenService tokenService = new TokenService();
            tokenService.verifyToken(jwtToken, response);
            filterChain.doFilter(request, response);
        } else {
            throw new InvalidDataException("Access token must be not null");
        }
    }

    /**
     * List URLs do not need authentication
     * @param url
     * @return
     */
    private boolean permitUrl(String url) {
        List<String> permitUrls = List.of("/actuator", "/swagger-ui", "/v3/", "/auth/");
        return permitUrls.stream().anyMatch(url::contains);
    }
}
