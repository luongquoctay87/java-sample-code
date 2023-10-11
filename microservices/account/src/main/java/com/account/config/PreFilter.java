package com.account.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j(topic = "PRE-FILTER")
public class PreFilter extends OncePerRequestFilter {

    @Value("${website.domainName}")
    private String allowOrigins;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!response.containsHeader("Access-Control-Allow-Origin")) {
            response.setHeader("Access-Control-Allow-Origin", allowOrigins);
        }
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.addHeader("Access-Control-Expose-Headers", "xsrf-token");

        StringBuffer url = new StringBuffer(request.getRequestURL());

        if (StringUtils.hasLength(request.getQueryString())) {
            url.append("?");
            url.append(request.getQueryString());
        }

        /*if ("POST".equalsIgnoreCase(request.getMethod())) {
            byte[] requestBody = StreamUtils.copyToByteArray(request.getInputStream());
            String jsonBody = new String(requestBody, StandardCharsets.UTF_8);
            url.append("\n");
            url.append(jsonBody);
        }*/

        if (url.toString().contains("/accounts")) {
            log.info("Request {} {}", request.getMethod(), url);
        }

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
