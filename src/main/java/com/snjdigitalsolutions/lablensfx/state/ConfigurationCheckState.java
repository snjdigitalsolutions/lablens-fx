package com.snjdigitalsolutions.lablensfx.state;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationCheckState {

    private final StringProperty checkStatus = new SimpleStringProperty("Not Checked");

    public String getCheckStatus() {
        return checkStatus.get();
    }

    public StringProperty checkStatusProperty() {
        return checkStatus;
    }
}
