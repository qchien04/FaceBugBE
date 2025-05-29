package com.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum  MediaType {
    NONE, VIDEO, IMAGE;

    @JsonCreator
    public static MediaType from(String s) {
        return MediaType.valueOf(s.toUpperCase());
    }
}
