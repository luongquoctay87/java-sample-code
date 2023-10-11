package com.sample.controller.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class MobileSignUpRequest implements Serializable {

    @NotBlank(message = "{msg-validate-phone}")
    private String phone;

    @NotBlank(message = "{msg-validate-password}")
    private String password;

    @NotBlank(message = "deviceToken must be not blank")
    private String deviceToken;
}
