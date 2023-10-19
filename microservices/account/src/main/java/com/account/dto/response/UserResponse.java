package com.account.dto.response;

import com.account.dto.Address;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserResponse implements Serializable {

    private int id;

    private String fullName;

    private String phone;

    private String email;

    private Address address;
}
