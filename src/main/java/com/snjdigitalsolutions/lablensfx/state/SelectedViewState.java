package com.snjdigitalsolutions.lablensfx.state;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.springframework.stereotype.Component;

@Component
public class SelectedViewState {

    private final ObjectProperty<ApplicationView> selectedView = new SimpleObjectProperty<>(ApplicationView.DASHBOARD);

    public ApplicationView getSelectedView() {
        return selectedView.get();
    }

    public ObjectProperty<ApplicationView> selectedViewProperty() {
        return selectedView;
    }
}
