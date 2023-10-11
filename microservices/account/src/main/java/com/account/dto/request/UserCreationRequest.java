package com.account.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class UserCreationRequest implements Serializable {

    @NotBlank(message = "email must be not blank")
    private String email;

    @NotBlank(message = "username must be not blank")
    private String username;

    @NotBlank(message = "password must be not blank")
    private String password;
}
