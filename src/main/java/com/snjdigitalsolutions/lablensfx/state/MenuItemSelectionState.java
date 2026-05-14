package com.snjdigitalsolutions.lablensfx.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.stereotype.Component;

@Component
public class MenuItemSelectionState {

    private final BooleanProperty confirmConfigurationChangeSelection = new SimpleBooleanProperty(true);

    /**
     * Returns whether the user should be prompted when changing configuration selection.
     *
     * @return {@code true} if confirmation is required
     */
    public boolean isConfirmConfigurationChangeSelection() {
        return confirmConfigurationChangeSelection.get();
    }

    /**
     * Returns the observable property for the confirm-on-config-change setting.
     *
     * @return the confirm-configuration-change-selection property
     */
    public BooleanProperty confirmConfigurationChangeSelectionProperty() {
        return confirmConfigurationChangeSelection;
    }
}
