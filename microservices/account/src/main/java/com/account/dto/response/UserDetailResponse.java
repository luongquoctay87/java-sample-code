package com.account.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserDetailResponse implements Serializable {

    private long id;

    private String fullName;

    private String phone;

    private String email;
}
