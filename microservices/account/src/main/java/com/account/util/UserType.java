package com.account.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum UserType {
    @JsonProperty("sysadmin")
    SYSADMIN("sysadmin"),

    @JsonProperty("admin")
    ADMIN("admin"),

    @JsonProperty("manager")
    MANAGER("manager"),

    @JsonProperty("user")
    USER("user");

    private final String name;

    UserType(String name) {
        this.name = name;
    }
}
