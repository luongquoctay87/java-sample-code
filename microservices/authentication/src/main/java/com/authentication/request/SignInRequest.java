package com.authentication.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {

    @NotBlank(message = "Username must be not blank")
    private String username;

    @NotBlank(message = "Password must be not blank")
    private String password;
}
