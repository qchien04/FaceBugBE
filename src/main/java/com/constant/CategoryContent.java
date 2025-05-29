package com.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CategoryContent {
    USER,
    COMEDY,
    GAME,
    VLOG,
    MOVIE,
    MUSIC,
    EDUCATION,
    LIFESTYLE,
    REVIEW,
    REACTION,
    SHORT_FILM,
    FOOD,
    TRAVEL,
    FASHION,
    BEAUTY,
    PET,
    OTHER;
    @JsonCreator
    public static MediaType from(String s) {
        return MediaType.valueOf(s.toUpperCase());
    }
}
