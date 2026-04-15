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
public class StatusBarState {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusBarState.class);

    //TODO selected and status bar should be separate state classes
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

    public ApplicationView getSelectedApplicationView() {
        return selectedApplicationView.get();
    }

    public ObjectProperty<ApplicationView> selectedApplicationViewProperty() {
        return selectedApplicationView;
    }

    public void setHostPanelAsOnlySelection(HostPanel panel) {
        getSelectedHostPanelList().clear();
        getSelectedHostPanelList().add(panel);
    }

}
