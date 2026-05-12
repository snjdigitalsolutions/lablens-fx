package com.snjdigitalsolutions.lablensfx.setting;

public enum Interval {

    MINUTES("Minutes"),
    HOURS("Hours"),
    DAYS("Days");

    private final String displayValue;

    Interval(String displayValue) {
        this.displayValue = displayValue;
    }

    public String displayValue(){
        return this.displayValue;
    }

}
