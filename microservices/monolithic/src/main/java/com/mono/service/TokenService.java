package com.mono.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mono.dto.request.SignInRequest;
import com.mono.dto.response.TokenResponse;
import com.mono.exception.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.mono.utils.Constant.JWT_SECRET_KEY;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
public class TokenService {
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Authorize user and get access token
     *
     * @param request
     * @return token
     */
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("Processing authenticate user, email={}", request.getUsername());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        User user = (User) authentication.getPrincipal();
        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        String accessToken = createToken("accessToken", user.getUsername(), authorities.toString());
        String refreshToken = createToken("refreshToken", user.getUsername(), authorities.toString());

        // Todo: Might be get more user from database

        return TokenResponse.builder()
                .username(user.getUsername())
                .roles(authorities.toString())
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

        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET_KEY.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = verifier.verify(refreshToken);
            String roles = decodedJWT.getClaim("roles").asString();
            String username = decodedJWT.getSubject();
            String accessToken = createToken("accessToken", username, roles);

            return TokenResponse.builder()
                    .username(username)
                    .roles(roles)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (JWTVerificationException e) {
            log.error("Generate refresh token was error, message={}", e.getMessage());
            throw new InvalidDataException(e.getMessage());
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
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET_KEY.getBytes());

        // Access Token will be expired in 60 minutes
        Date expiryDate = new Date(System.currentTimeMillis() + 3600 * 1000);
        if (tokenType.equals("refreshToken")) {
            // Refresh Token will be expired in 30 days
            expiryDate = new Date(System.currentTimeMillis() + 30L * 86400 * 1000);
        }

        return JWT.create()
                .withIssuer("API_TOKEN") // identifies the party that created the token and signed it
                .withSubject(subject) // identifies the subject of the JWT
                .withClaim("roles", claim) // used to set any custom claim
                .withIssuedAt(new Date()) // identifies the time at which the JWT was created; we can use this to determine the age of the JWT
                .withExpiresAt(expiryDate) // identifies the expiration time of the JWT
                .withJWTId(UUID.randomUUID().toString()) // unique identifier for the JWT
                .withNotBefore(new Date(System.currentTimeMillis() + 1000L)) // identifies the time before which the JWT should not be accepted for processing
                .sign(algorithm);
    }

    /**
     * Verify Token
     *
     * @param jwtToken
     * @param response
     * @throws IOException
     */
    public void verifyToken(String jwtToken, HttpServletResponse response) throws IOException {
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET_KEY.getBytes());
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
