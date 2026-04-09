package com.snjdigitalsolutions.lablensfx.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.stereotype.Component;

@Component
public class SettingState {

    private final BooleanProperty settingsLoaded = new SimpleBooleanProperty(false);
    private final BooleanProperty showIPs = new SimpleBooleanProperty(true);
    private final BooleanProperty promptWhenConfigSelectionChanges = new SimpleBooleanProperty(true);

    public boolean isSettingsLoaded() {
        return settingsLoaded.get();
    }

    public BooleanProperty settingsLoadedProperty() {
        return settingsLoaded;
    }

    public boolean isShowIPs() {
        return showIPs.get();
    }

    public BooleanProperty showIPsProperty() {
        return showIPs;
    }

    public boolean isPromptWhenConfigSelectionChanges() {
        return promptWhenConfigSelectionChanges.get();
    }

    public BooleanProperty promptWhenConfigSelectionChangesProperty() {
        return promptWhenConfigSelectionChanges;
    }
}
