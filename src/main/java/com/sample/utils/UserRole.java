package com.sample.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum UserRole {
    @JsonProperty("sysadmin")
    SYSADMIN("sysadmin"),

    @JsonProperty("admin")
    ADMIN("admin"),

    @JsonProperty("manager")
    MANAGER("manager"),

    @JsonProperty("user")
    USER("user");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }
}
