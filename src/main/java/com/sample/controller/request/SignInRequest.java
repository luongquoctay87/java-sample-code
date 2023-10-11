package com.sample.controller.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class SignInRequest implements Serializable {

    @NotBlank(message = "Username must be not blank")
    private String username;

    @NotBlank(message = "Password must be not blank")
    private String password;
}
