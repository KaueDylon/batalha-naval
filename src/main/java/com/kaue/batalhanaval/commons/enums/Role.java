package com.kaue.batalhanaval.commons.enums;

public enum Role {
    PLAYER("player");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
