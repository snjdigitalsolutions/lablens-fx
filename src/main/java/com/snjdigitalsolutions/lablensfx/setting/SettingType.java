package com.snjdigitalsolutions.lablensfx.setting;

public enum SettingType {

    CONFIG_CONFIRMATION("confirm-config", true, true),
    SHOW_IP_ADDRESSES("show-ips", true, true);

    final String name;
    final boolean boolType;
    final Object defaultValue;

    /**
     * Creates a setting type descriptor.
     *
     * @param name         the unique setting key name
     * @param boolType     {@code true} if the setting holds a boolean value
     * @param defaultValue the default value to use when the setting is absent from the database
     */
    SettingType(String name,
                boolean boolType,
                Object defaultValue
    )
    {
        this.name = name;
        this.boolType = boolType;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the unique key name for this setting.
     *
     * @return the setting name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns whether this setting stores a boolean value.
     *
     * @return {@code true} if this is a boolean setting
     */
    public boolean isBoolType() {
        return this.boolType;
    }

    /**
     * Returns the default value used when this setting has not been explicitly saved.
     *
     * @return the default value
     */
    public Object getDefaultValue() {
        return this.defaultValue;
    }
}
