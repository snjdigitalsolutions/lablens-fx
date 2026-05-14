package com.snjdigitalsolutions.lablensfx.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.stereotype.Component;

@Component
public class ApplicationState {

    private final BooleanProperty loadingData = new SimpleBooleanProperty(false);

    /**
     * Returns whether a file-loading operation is currently in progress.
     *
     * @return {@code true} if data is loading
     */
    public boolean isLoadingData() {
        return loadingData.get();
    }

    /**
     * Returns the observable property indicating whether data is currently being loaded.
     *
     * @return the loading-data property
     */
    public BooleanProperty loadingDataProperty() {
        return loadingData;
    }
}
