package com.mono.controller;

import com.mono.dto.request.SignInRequest;
import com.mono.dto.response.TokenResponse;
import com.mono.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/auth")
public record AuthenticationController(TokenService tokenService) {

    @Operation(summary = "Get Access Token", description = "Return access token with expiry date 1 hour, refresh token with expiry date 1 month")
    @PostMapping(value = "/access-token", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public TokenResponse getAccessToken(@Validated @RequestBody SignInRequest request) {
        return tokenService.getAccessToken(request);
    }

    @Operation(summary = "Get Refresh Token", description = "Return access token with expiry date 1 hour")
    @PostMapping("/refresh-token")
    @ResponseStatus(OK)
    public TokenResponse getRefreshToken(@RequestParam String refreshToken) {
        return tokenService.getRefreshToken(refreshToken);
    }
}
