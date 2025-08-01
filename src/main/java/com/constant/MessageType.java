package com.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MessageType {
    TEXT, IMAGE, NOTICE;
    @JsonCreator
    public static MessageType from(String s) {
        return MessageType.valueOf(s.toUpperCase());
    }
}
