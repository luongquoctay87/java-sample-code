package com.sample.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
public class Country implements Serializable {

    private String code;

    private String name;

    private String currency;

    @JsonIgnoreProperties
    private Integer population;

    @JsonIgnoreProperties
    private String capital;

    private String continent;
}
