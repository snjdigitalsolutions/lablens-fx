package com.snjdigitalsolutions.lablensfx.setting;

public enum Interval {

    MINUTES("Minutes", 60),
    HOURS("Hours", 3600),
    DAYS("Days", 86400);

    private final String displayValue;
    private final long multiplier;


    Interval(String displayValue,  long multiplier) {
        this.displayValue = displayValue;
        this.multiplier = multiplier;
    }

    /**
     * Returns the human-readable label for this interval (e.g. {@code "Minutes"}).
     *
     * @return the display value string
     */
    public String displayValue(){
        return this.displayValue;
    }

    /**
     * Returns the number of seconds that one unit of this interval represents.
     * For example, {@link #HOURS} returns {@code 3600}.
     *
     * @return the seconds-per-unit multiplier
     */
    public long multiplier() {
        return this.multiplier;
    }

}
