## Json Web Token with Spring Security

### 1. Add dependencies

```
<!-- spring security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<!-- java-jwt -->
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>4.0.0</version>
</dependency>
```

### 2. Create file `RequestFilter extends OncePerRequestFilter`

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

### 3. Create file `SecurityConfig`

```
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String[] WHITE_LIST = {"/auth/**"};

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v3/**")
                .antMatchers("/actuator/**")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/swagger-ui/**")
                .antMatchers("/v3/api-docs/**");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests().antMatchers(WHITE_LIST).permitAll();
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilterBefore(new RequestFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}

```

### 4. Add config in `application.properties`

```
# SPRING SECURITY
spring.security.user.name=admin
spring.security.user.password=password

# JSON WEB TOKEN
jwt.token.secretKey=Api@Secret.Key
jwt.token.validity=60
jwt.refresh.token.validity=30
jwt.reset.token.validity=60
```


### 5. Create file `SignInService`

```
@Service
@Slf4j
public class SignInService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${jwt.token.secretKey}")
    private String secretKey;
    @Value("${jwt.token.validity}")
    private long jwtTokenValidity;
    @Value("${jwt.refresh.token.validity}")
    private long jwtRefreshTokenValidity;

    /**
     * Authorize user and get access token
     *
     * @param request
     * @return token
     */
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("Processing authenticate user, email={}", request.getUsername());

        // TODO: check from database
        String userId = "USER_ID";

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        User user = (User) authentication.getPrincipal();
        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        String accessToken = createToken("accessToken", user.getUsername(), userId);
        String refreshToken = createToken("refreshToken", user.getUsername(), userId);

        return TokenResponse.builder()
                .userId(userId)
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Get refresh token
     *
     * @param refreshToken
     * @return refresh token
     */
    public TokenResponse getRefreshToken(String refreshToken) {
        log.info("Processing generate refresh token");

        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = verifier.verify(refreshToken);
            String userId = decodedJWT.getClaim("userId").asString();
            String username = decodedJWT.getSubject();
            String accessToken = createToken("accessToken", username, userId);

            return TokenResponse.builder()
                    .userId(userId)
                    .username(username)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (JWTVerificationException e) {
            log.error("Generate refresh token was error, message={}", e.getMessage());
            throw new InvalidParameterException(e.getMessage());
        }
    }

    /**
     * Generate access token and refresh token
     *
     * @param tokenType refreshToken | accessToken
     * @param subject
     * @param claim
     * @return
     */
    private String createToken(String tokenType, String subject, String claim) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());

        Date validityPeriod = new Date(System.currentTimeMillis() + jwtTokenValidity * 60 * 1000);
        if (tokenType.equals("refreshToken")) {
            validityPeriod = new Date(System.currentTimeMillis() + jwtRefreshTokenValidity * 24 * 60 * 60 * 1000);
        }

        return JWT.create()
                .withIssuer("API_TOKEN") // identifies the party that created the token and signed it
                .withSubject(subject) // identifies the subject of the JWT
                .withClaim("userId", claim) // used to set any custom claim
                .withIssuedAt(new Date()) // identifies the time at which the JWT was created; we can use this to determine the age of the JWT
                .withExpiresAt(validityPeriod) // identifies the expiration time of the JWT
                .withJWTId(UUID.randomUUID().toString()) // unique identifier for the JWT
                .withNotBefore(new Date(System.currentTimeMillis() + 1000L)) // identifies the time before which the JWT should not be accepted for processing
                .sign(algorithm);
    }
}
```

### 6. Create file `UserService implements UserDetailsService`

```
@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SERVICE")
public class UserService implements UserDetailsService {

    private final SampleUserRepository userRepository;

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SampleUser user = userRepository.findByEmail(username);
        if (user == null) {
            log.warn("User not found in the database");
            throw new UsernameNotFoundException("User not found");
        }

        return new User(user.getEmail(), user.getPassword(), getAuthority(username));
    }

    private List<SimpleGrantedAuthority> getAuthority(String username) {
        // Todo: get authorities from database
        return List.of(new SimpleGrantedAuthority("Admin"));
    }
}
```


### 7. Create file `SignInRequest`

```
@Data
public class SignInRequest implements Serializable {

    @NotBlank(message = "Username must be not blank")
    private String username;

    @NotBlank(message = "Password must be not blank")
    private String password;
}
```


### 8. Create file `TokenResponse`

```
@Data
@Builder
public class TokenResponse implements Serializable {
    private String userId;
    private String username;
    private String accessToken;
    private String refreshToken;
}
```

### 9. Create file `LoginController`

```
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final SignInService signInService;

    @Operation(summary = "Get Access Token", description = "Return access token with expiry date 1 hour, refresh token with expiry date 1 month")
    @PostMapping("/access-token")
    @ResponseStatus(OK)
    public TokenResponse getAccessToken(@Validated @RequestBody SignInRequest request) {
        return signInService.getAccessToken(request);
    }

    @Operation(summary = "Get Refresh Token", description = "Return access token with expiry date 1 hour")
    @PostMapping("/refresh-token")
    @ResponseStatus(OK)
    public TokenResponse getRefreshToken(@RequestParam String refreshToken) {
        return signInService.getRefreshToken(refreshToken);
    }
}
```

***Reference sources:***
- [Managing JWT With Auth0 java-jwt](https://www.baeldung.com/java-auth0-jwt)
- [Spring Security JWT Authentication & Authorization](https://medium.com/code-with-farhan/spring-security-jwt-authentication-authorization-a2c6860be3cf)
