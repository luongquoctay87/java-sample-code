package com.account.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Set;

@Data
public class UserCreationRequest implements Serializable {

    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @NotBlank(message = "lastName must be not blank")
    private String lastName;

    @NotBlank(message = "dateOfBirth must be not blank")
    private String dateOfBirth;

    @NotBlank(message = "phone must be not blank")
    private String phone;

    @NotBlank(message = "email must be not blank")
    private String email;

    @NotBlank(message = "username must be not blank")
    private String username;

    @NotBlank(message = "password must be not blank")
    private String password;

    private Set<Address> addresses;

    @Data
    public static class Address {
        private String apartmentNumber;
        private String floor;
        private String building;
        private String streetNumber;
        private String street;
        private String city;
        private String country;
        private Integer addressType;
    }
}
