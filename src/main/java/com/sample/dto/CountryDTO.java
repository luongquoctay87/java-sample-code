package com.sample.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CountryDTO implements Serializable {
    private String countryCode;
    private String countryName;
    private String currencyCode;
}
