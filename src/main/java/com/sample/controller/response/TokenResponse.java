package com.sample.controller.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TokenResponse implements Serializable {
    private String userId;
    private String username;
    private String accessToken;
    private String refreshToken;
}
