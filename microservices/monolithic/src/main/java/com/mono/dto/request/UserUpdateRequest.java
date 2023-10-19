package com.mono.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
public class UserUpdateRequest implements Serializable {

    @Min(value = 1, message = "Id must be greater than or equal to 1")
    private Long id;

    private String username;

    private String password;

    private Boolean enabled;
}
