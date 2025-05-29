package com.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FollowState {
    NONE,
    FOLLOW;

    @JsonCreator
    public static FollowState from(String s) {
        return FollowState.valueOf(s.toUpperCase());
    }
}
