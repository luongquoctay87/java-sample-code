package com.account.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
