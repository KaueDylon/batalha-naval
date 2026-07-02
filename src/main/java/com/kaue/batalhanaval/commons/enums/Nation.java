package com.kaue.batalhanaval.commons.enums;

public enum Nation {

    USA("United States"),
    UK("United Kingdom"),
    USSR("Soviet Union"),
    GERMANY("Germany"),
    JAPAN("Japan"),
    ITALY("Italy");

    private final String displayName;

    Nation(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
