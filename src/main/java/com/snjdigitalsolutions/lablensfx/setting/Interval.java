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

    public String displayValue(){
        return this.displayValue;
    }

    public long multiplier() {
        return this.multiplier;
    }

}
