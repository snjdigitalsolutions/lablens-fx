package com.snjdigitalsolutions.lablensfx.state;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.springframework.stereotype.Component;

@Component
public class SelectedViewState {

    private final ObjectProperty<ApplicationView> selectedView = new SimpleObjectProperty<>(ApplicationView.DASHBOARD);

    /**
     * Returns the currently selected application view.
     *
     * @return the active {@link ApplicationView}
     */
    public ApplicationView getSelectedView() {
        return selectedView.get();
    }

    /**
     * Returns the observable property for the active application view.
     *
     * @return the selected-view property
     */
    public ObjectProperty<ApplicationView> selectedViewProperty() {
        return selectedView;
    }
}
