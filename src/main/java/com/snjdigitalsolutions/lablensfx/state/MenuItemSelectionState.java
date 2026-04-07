package com.snjdigitalsolutions.lablensfx.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.stereotype.Component;

@Component
public class MenuItemSelectionState {

    private final BooleanProperty confirmConfigurationChangeSelection = new SimpleBooleanProperty(true);

    public boolean isConfirmConfigurationChangeSelection() {
        return confirmConfigurationChangeSelection.get();
    }

    public BooleanProperty confirmConfigurationChangeSelectionProperty() {
        return confirmConfigurationChangeSelection;
    }
}
