package com.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Privacy {
    PUBLIC, PRIVATE;

    @JsonCreator
    public static Privacy fromString(String value) {
        return Privacy.valueOf(value.toUpperCase());
    }
}

