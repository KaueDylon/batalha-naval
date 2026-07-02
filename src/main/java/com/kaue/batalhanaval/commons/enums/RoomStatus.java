package com.kaue.batalhanaval.commons.enums;

public enum RoomStatus {
    WAITING("waiting"),
    FULL("full"),
    IN_GAME("in_game");

    private final String value;

    RoomStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
