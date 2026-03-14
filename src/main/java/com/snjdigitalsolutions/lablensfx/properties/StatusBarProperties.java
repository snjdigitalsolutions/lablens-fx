package com.snjdigitalsolutions.lablensfx.properties;

import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StatusBarProperties implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusBarProperties.class);
    private final StringProperty statusProperty = new SimpleStringProperty("");
    private final IntegerProperty numberOfSelectedHostsProperty = new SimpleIntegerProperty(0);
    private final ObjectProperty<ApplicationView> selectedApplicationViewProperty = new SimpleObjectProperty<>(ApplicationView.DASHBOARD);
    private final BooleanProperty disableDeleteHostMenuItemProperty = new SimpleBooleanProperty(true);

    public StringProperty statusProperty() {
        return statusProperty;
    }

    public IntegerProperty numberOfSelectedHostsProperty() {
        return numberOfSelectedHostsProperty;
    }

    public BooleanProperty disableDeleteHostMenuItemProperty() {
        return disableDeleteHostMenuItemProperty;
    }

    @Override
    public void performIntialization() {
        numberOfSelectedHostsProperty.addListener((obj, oldVal, newVal) -> {
            if (selectedApplicationViewProperty.get().equals(ApplicationView.DASHBOARD) && newVal.intValue() > 0) {
                LOGGER.debug("{} Hosts Selected", newVal);
                statusProperty.setValue(newVal + " Hosts Selected");
                disableDeleteHostMenuItemProperty.setValue(false);
            } else if (selectedApplicationViewProperty.get().equals(ApplicationView.DASHBOARD) && newVal.intValue() == 0) {
                statusProperty.setValue("");
                disableDeleteHostMenuItemProperty.setValue(true);
            }
        });
    }

}
