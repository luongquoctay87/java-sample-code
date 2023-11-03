package com.sample.controller.request;

import com.sample.utils.UserRole;
import com.sample.validator.ValueOfEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
public class ValidateRequest implements Serializable {
    @NotBlank(message = "firstName must be not null")
    private String firstName;

    @NotBlank(message = "lastName must be not null")
    private String lastName;

    @ValueOfEnum(name = "role", regexp = "(SYSADMIN | ADMIN | MANAGER | USER)", enumClass = UserRole.class)
    private String role;
}
