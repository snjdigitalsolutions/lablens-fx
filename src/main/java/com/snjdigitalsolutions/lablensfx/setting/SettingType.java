package com.snjdigitalsolutions.lablensfx.setting;

public enum SettingType {

    CONFIG_CONFIRMATION("confirm-config", true, true),
    SHOW_IP_ADDRESSES("show-ips", true, true);

    final String name;
    final boolean boolType;
    final Object defaultValue;

    SettingType(String name,
                boolean boolType,
                Object defaultValue
    )
    {
        this.name = name;
        this.boolType = boolType;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public boolean isBoolType() {
        return this.boolType;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }
}
