package com.sample.controller.request;

import com.sample.model.nestedObject.Address;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SampleUserRequest implements Serializable {
    private String rangeKey;

    @NotBlank(message = "{msg-validate-firstName}")
    private String firstName;

    @NotBlank(message = "{msg-validate-lastName}")
    private String lastName;

    @NotBlank(message = "{msg-validate-phone}")
    private String phone;

    @NotBlank(message = "{msg-validate-email}")
    private String email;

    @NotBlank(message = "{msg-validate-password}")
    private String password;

    @NotNull(message = "{msg-validate-address}")
    private Address address;
}
