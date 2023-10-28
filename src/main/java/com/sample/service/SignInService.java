package com.sample.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sample.controller.request.SignInRequest;
import com.sample.controller.response.TokenResponse;
import com.sample.exception.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
