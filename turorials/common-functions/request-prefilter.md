## Define a Filter before request API

### Example 1: In this tutorial, we'll define a filter to check JWT before request an API

```
@Slf4j(topic = "REQUEST-FILTER")
public class RequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("======> " + request.getServletPath());

        if (request.getServletPath().contains("/auth/")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizeHeader = request.getHeader(AUTHORIZATION);
            if (authorizeHeader != null && authorizeHeader.startsWith("Bearer ")) {
                String jwtToken = authorizeHeader.substring("Bearer ".length());
                verifyToken(jwtToken, response);
                filterChain.doFilter(request, response);
            }
        }
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

```


### Example 2: Add CORS in Filter at api-gateway
```
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
```


***Reference sources:***
- [How to Define a Spring Boot Filter?](https://www.baeldung.com/spring-boot-add-filter)

