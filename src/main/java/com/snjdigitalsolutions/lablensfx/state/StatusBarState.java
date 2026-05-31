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

    /**
     * Returns the observable string property for the current status bar message.
     *
     * @return the status property
     */
    public StringProperty statusProperty() {
        return statusMessage;
    }

    /**
     * Returns the observable integer property representing the number of selected hosts.
     *
     * @return the number-of-selected-hosts property
     */
    public IntegerProperty numberOfSelectedHostsProperty() {
        return numberOfSelectedHosts;
    }

    /**
     * Returns the observable boolean property controlling whether the delete-host menu item is disabled.
     *
     * @return the disable-delete-host-menu-item property
     */
    public BooleanProperty disableDeleteHostMenuItemProperty() {
        return disableDeleteHostMenuItem;
    }

    /**
     * Returns the observable list of currently selected host panels.
     *
     * @return the selected host panel list
     */
    public ObservableList<HostPanel> getSelectedHostPanelList() {
        return selectedHostPanelList.get();
    }

    /**
     * Returns the observable list property of selected host panels.
     *
     * @return the selected-host-panel-list property
     */
    public ListProperty<HostPanel> selectedHostPanelListProperty() {
        return selectedHostPanelList;
    }

    /**
     * Returns the currently selected application view.
     *
     * @return the active {@link ApplicationView}
     */
    public ApplicationView getSelectedApplicationView() {
        return selectedApplicationView.get();
    }

    /**
     * Returns the observable property for the selected application view.
     *
     * @return the selected-application-view property
     */
    public ObjectProperty<ApplicationView> selectedApplicationViewProperty() {
        return selectedApplicationView;
    }

    /**
     * Clears the current selection and sets the given panel as the sole selected host.
     *
     * @param panel the host panel to select
     */
    public void setHostPanelAsOnlySelection(HostPanel panel) {
        getSelectedHostPanelList().clear();
        getSelectedHostPanelList().add(panel);
    }

}
