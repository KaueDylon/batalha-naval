package com.kaue.batalhanaval.commons.enums;

public enum Phase {
    SETUP("setup"),
    PLAYING("playing"),
    FINISHED("finished");

    private final String value;

    Phase(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
