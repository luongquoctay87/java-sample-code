package com.sample.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum UserStatus {
    @JsonProperty("active")
    ACTIVE("active"),

    @JsonProperty("inactive")
    INACTIVE("inactive"),

    @JsonProperty("none")
    NONE("none");

    private final String name;

    UserStatus(String name) {
        this.name = name;
    }
}
