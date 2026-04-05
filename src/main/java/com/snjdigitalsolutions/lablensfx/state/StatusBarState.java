package com.snjdigitalsolutions.lablensfx.state;

import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StatusBarState implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusBarState.class);

    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final ListProperty<HostPanel> selectedHostPanelList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty numberOfSelectedHosts = new SimpleIntegerProperty(0);
    private final ObjectProperty<ApplicationView> selectedApplicationView = new SimpleObjectProperty<>(ApplicationView.DASHBOARD);
    private final BooleanProperty disableDeleteHostMenuItem = new SimpleBooleanProperty(true);

    public StringProperty statusProperty() {
        return statusMessage;
    }

    public IntegerProperty numberOfSelectedHostsProperty() {
        return numberOfSelectedHosts;
    }

    public BooleanProperty disableDeleteHostMenuItemProperty() {
        return disableDeleteHostMenuItem;
    }

    public ObservableList<HostPanel> getSelectedHostPanelList() {
        return selectedHostPanelList.get();
    }

    public ListProperty<HostPanel> selectedHostPanelListProperty() {
        return selectedHostPanelList;
    }

    @Override
    public void performIntialization() {
        numberOfSelectedHosts.addListener((obj, oldVal, newVal) -> {
            if (selectedApplicationView.get().equals(ApplicationView.DASHBOARD) && newVal.intValue() > 0) {
                LOGGER.debug("{} Hosts Selected", newVal);
                statusMessage.setValue("Hosts Selected: " + newVal);
                disableDeleteHostMenuItem.setValue(false);
            } else if (selectedApplicationView.get().equals(ApplicationView.DASHBOARD) && newVal.intValue() == 0) {
                statusMessage.setValue("");
                disableDeleteHostMenuItem.setValue(true);
            }
        });
    }

}
