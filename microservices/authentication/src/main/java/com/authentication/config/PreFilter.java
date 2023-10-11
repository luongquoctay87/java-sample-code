package com.authentication.config;

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
import java.util.List;

@Component
@Slf4j(topic = "PRE-FILTER")
public class PreFilter extends OncePerRequestFilter {

    @Value("${website.domainName}")
    private String domainName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!response.containsHeader("Access-Control-Allow-Origin")) {
            response.setHeader("Access-Control-Allow-Origin", domainName);
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

        if (url.toString().contains("/authz")) {
            log.info("Request {} {}", request.getMethod(), url);
        }

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
