package com.snjdigitalsolutions.lablensfx.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.stereotype.Component;

@Component
public class ShowIpAddressState {

    private final BooleanProperty showIpProperty = new SimpleBooleanProperty(true);

    public boolean isShowIpProperty() {
        return showIpProperty.get();
    }

    public BooleanProperty showIpPropertyProperty() {
        return showIpProperty;
    }
}
