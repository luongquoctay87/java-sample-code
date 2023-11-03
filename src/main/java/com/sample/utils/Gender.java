package com.sample.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum Gender {
    @JsonProperty("male")
    MALE("male"),

    @JsonProperty("female")
    FEMALE("female"),

    @JsonProperty("other")
    OTHER("other");

    private final String name;

    Gender(String name) {
        this.name = name;
    }
}
