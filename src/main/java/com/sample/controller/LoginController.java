package com.sample.controller;

import com.sample.controller.request.SignInRequest;
import com.sample.controller.response.TokenResponse;
import com.sample.service.SignInService;
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
