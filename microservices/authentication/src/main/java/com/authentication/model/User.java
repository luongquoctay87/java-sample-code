package com.authentication.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private Long id;
    private String fistName;
    private String lastName;
    private String phone;
    private String email;
    private String username;
    private String password;
    private String role;

}
