package com.mono.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserDetailResponse implements Serializable {
    private long id;
    private String email;
    private String username;
}
