package com.kaue.batalhanaval.commons.enums;

public enum NationPortrait {
    // USA
    USA_GENERAL("usa_general", Nation.USA),
    USA_ADMIRAL("usa_admiral", Nation.USA),
    USA_PILOT("usa_pilot", Nation.USA),

    // UK
    UK_GENERAL("uk_general", Nation.UK),
    UK_ADMIRAL("uk_admiral", Nation.UK),
    UK_PILOT("uk_pilot", Nation.UK),

    // USSR
    USSR_GENERAL("ussr_general", Nation.USSR),
    USSR_ADMIRAL("ussr_admiral", Nation.USSR),
    USSR_PILOT("ussr_pilot", Nation.USSR),

    // GERMANY
    GERMANY_GENERAL("germany_general", Nation.GERMANY),
    GERMANY_ADMIRAL("germany_admiral", Nation.GERMANY),
    GERMANY_PILOT("germany_pilot", Nation.GERMANY),

    // JAPAN
    JAPAN_GENERAL("japan_general", Nation.JAPAN),
    JAPAN_ADMIRAL("japan_admiral", Nation.JAPAN),
    JAPAN_PILOT("japan_pilot", Nation.JAPAN),

    // ITALY
    ITALY_GENERAL("italy_general", Nation.ITALY),
    ITALY_ADMIRAL("italy_admiral", Nation.ITALY),
    ITALY_PILOT("italy_pilot", Nation.ITALY);

    private final String id;
    private final Nation nation;

    NationPortrait(String id, Nation nation) {
        this.id = id;
        this.nation = nation;
    }

    public String getId() { return id; }
    public Nation getNation() { return nation; }
}
