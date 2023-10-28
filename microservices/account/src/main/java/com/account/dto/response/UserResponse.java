package com.account.dto.response;

import com.account.dto.AddressDTO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserResponse implements Serializable {

    private long id;

    private String fullName;

    private String phone;

    private String email;

    private AddressDTO addressDTO;
}
