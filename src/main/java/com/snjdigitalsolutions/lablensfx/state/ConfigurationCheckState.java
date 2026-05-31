package com.snjdigitalsolutions.lablensfx.state;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationCheckState {

    private final StringProperty checkStatus = new SimpleStringProperty("Not Checked");

    /**
     * Returns the current configuration check status message.
     *
     * @return the check status string (e.g. {@code "Not Checked"})
     */
    public String getCheckStatus() {
        return checkStatus.get();
    }

    /**
     * Returns the observable {@link StringProperty} for the configuration check status,
     * allowing UI components to bind directly to status changes.
     *
     * @return the check status property
     */
    public StringProperty checkStatusProperty() {
        return checkStatus;
    }
}
