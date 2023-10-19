package com.mono.controller;

import com.mono.dto.request.SignInRequest;
import com.mono.dto.response.TokenResponse;
import com.mono.dto.response.UserDetailResponse;
import com.mono.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller")
public record AuthenticationController(TokenService tokenService) {

    @Operation(summary = "Get Access Token", description = "Return access token with expiry date 1 hour, refresh token with expiry date 1 month")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = { @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenResponse.class),
                            examples = @ExampleObject(name = "Return token",
                                    summary = "Retrieved successfully",
                                    value = "{\n" +
                                            "  \"username\": \"admin\",\n" +
                                            "  \"roles\": \"[Admin]\",\n" +
                                            "  \"accessToken\": \"eyJ0eXAiOiJKV1Qi...\",\n" +
                                            "  \"refreshToken\": \"eyJ0eXAiOiJKV1QiLyqA...\"\n" +
                                            "}")
                    ) })
    })
    @PostMapping(value = "/access-token", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public TokenResponse getAccessToken(@Validated @RequestBody SignInRequest request) {
        return tokenService.getAccessToken(request);
    }

    @Operation(summary = "Get Refresh Token", description = "Return access token with expiry date 1 hour")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = { @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenResponse.class),
                            examples = @ExampleObject(name = "Return token",
                                    summary = "Retrieved successfully",
                                    value = "{\n" +
                                            "  \"username\": \"admin\",\n" +
                                            "  \"roles\": \"[Admin]\",\n" +
                                            "  \"accessToken\": \"eyJ0eXAiOiJKV1Qi...\",\n" +
                                            "  \"refreshToken\": \"eyJ0eXAiOiJKV1QiLyqA...\"\n" +
                                            "}")
                    ) })
    })
    @PostMapping("/refresh-token")
    @ResponseStatus(OK)
    public TokenResponse getRefreshToken(@RequestParam String refreshToken) {
        return tokenService.getRefreshToken(refreshToken);
    }
}
