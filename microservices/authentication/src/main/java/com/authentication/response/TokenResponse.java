package com.authentication.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class TokenResponse implements Serializable {
    private String userId;
    private String username;
    private String role;
    private String accessToken;
    private String refreshToken;
}
