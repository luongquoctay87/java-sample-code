package com.account.dto.request;

import com.account.dto.AddressDTO;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
public class UserUpdateRequest implements Serializable {
    @Min(value = 1, message = "Id must be greater than or equal to 1")
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    private AddressDTO addressDTO;
}
