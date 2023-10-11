package com.account.dto.response;

import com.account.dto.Address;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private int id;

    private String fullName;

    private String phone;

    private String email;

    private Address address;
}
