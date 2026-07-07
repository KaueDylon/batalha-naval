package com.kaue.batalhanaval.commons.enums;

public enum ShipType {
    CARRIER(5, "Porta-Aviões"),
    BATTLESHIP(4, "Encouraçado"),
    CRUISER(3, "Cruzador"),
    SUBMARINE(3, "Submarino"),
    DESTROYER(2, "Destroyer");

    private final int size;
    private final String displayName;

    ShipType(int size, String displayName) {
        this.size = size;
        this.displayName = displayName;
    }

    public int getSize() { return size; }
    public String getDisplayName() { return displayName; }
}
