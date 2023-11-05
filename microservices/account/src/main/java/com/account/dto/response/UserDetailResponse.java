package com.account.dto.response;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDetailResponse implements Serializable {

    private long id;

    private String firstName;

    private String lastName;

    private String phone;

    private String email;
}
