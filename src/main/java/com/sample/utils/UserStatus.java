package com.sample.utils;

import lombok.Getter;

@Getter
public enum UserStatus {
    NONE("none"),
    ACTIVE("active"),
    INACTIVE("inactive");

    private String name;

    UserStatus(String name) {
        this.name = name;
    }
}
