package com.snjdigitalsolutions.lablensfx.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.stereotype.Component;

@Component
public class SettingState {

    private final BooleanProperty settingsLoaded = new SimpleBooleanProperty(false);
    private final BooleanProperty showIPs = new SimpleBooleanProperty(true);
    private final BooleanProperty promptWhenConfigSelectionChanges = new SimpleBooleanProperty(true);

    /**
     * Returns whether application settings have been loaded from the database.
     *
     * @return {@code true} if settings are loaded
     */
    public boolean isSettingsLoaded() {
        return settingsLoaded.get();
    }

    /**
     * Returns the observable property indicating whether settings have been loaded.
     *
     * @return the settings-loaded property
     */
    public BooleanProperty settingsLoadedProperty() {
        return settingsLoaded;
    }

    /**
     * Returns whether IP addresses should be displayed in the host list.
     *
     * @return {@code true} if IP addresses are visible
     */
    public boolean isShowIPs() {
        return showIPs.get();
    }

    /**
     * Returns the observable property for the show-IP-addresses setting.
     *
     * @return the show-IPs property
     */
    public BooleanProperty showIPsProperty() {
        return showIPs;
    }

    /**
     * Returns whether the user should be prompted when changing configuration selection.
     *
     * @return {@code true} if the prompt is enabled
     */
    public boolean isPromptWhenConfigSelectionChanges() {
        return promptWhenConfigSelectionChanges.get();
    }

    /**
     * Returns the observable property for the prompt-on-config-change setting.
     *
     * @return the prompt-when-config-selection-changes property
     */
    public BooleanProperty promptWhenConfigSelectionChangesProperty() {
        return promptWhenConfigSelectionChanges;
    }
}
