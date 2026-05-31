package com.snjdigitalsolutions.lablensfx.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.stereotype.Component;

@Component
public class ShowIpAddressState {

    private final BooleanProperty showIpProperty = new SimpleBooleanProperty(true);

    /**
     * Returns whether IP addresses should currently be shown.
     *
     * @return {@code true} if IP addresses are visible
     */
    public boolean isShowIpProperty() {
        return showIpProperty.get();
    }

    /**
     * Returns the observable property controlling IP address visibility.
     *
     * @return the show-IP property
     */
    public BooleanProperty showIpPropertyProperty() {
        return showIpProperty;
    }
}
