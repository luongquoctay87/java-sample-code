package com.account.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AddressDTO implements Serializable {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String text;
}
