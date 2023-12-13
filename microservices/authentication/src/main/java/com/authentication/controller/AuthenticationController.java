package com.authentication.controller;

import com.authentication.request.SignInRequest;
import com.authentication.response.TokenResponse;
import com.authentication.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthService authService;

    @Operation(summary = "Get Access Token", description = "Return access token with expiry date 1 hour, refresh token with expiry date 1 month")
    @PostMapping("/access-token")
    @ResponseStatus(OK)
    public TokenResponse getAccessToken(@Validated @RequestBody SignInRequest request) {
        return authService.getAccessToken(request);
    }

    @Operation(summary = "Get Refresh Token", description = "Return access token with expiry date 1 hour")
    @PostMapping("/refresh-token")
    @ResponseStatus(OK)
    public TokenResponse getRefreshToken(@RequestParam String refreshToken) {
        return authService.getRefreshToken(refreshToken);
    }
}
