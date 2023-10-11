package com.sample.dto;

import com.sample.model.nestedObject.Address;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SampleDTO implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Address address;
    private String createdAt;
    private String updatedAt;
}
