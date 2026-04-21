package com.snjdigitalsolutions.lablensfx.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.stereotype.Component;

@Component
public class ApplicationState {

    private final BooleanProperty loadingData = new SimpleBooleanProperty(false);

    public boolean isLoadingData() {
        return loadingData.get();
    }

    public BooleanProperty loadingDataProperty() {
        return loadingData;
    }
}
