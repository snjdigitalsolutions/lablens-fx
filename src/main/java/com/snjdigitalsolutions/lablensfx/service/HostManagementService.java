package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.nodes.HostFormPane;
import com.snjdigitalsolutions.lablensfx.nodes.HostPane;
import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.properties.GlobalProperties;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Service;

@Service
public class HostManagementService {

    private final HostPane hostPane;
    private final HostFormPane hostFormPane;
    private final GlobalProperties globalProperties;

    public HostManagementService(HostPane hostPane, HostFormPane hostFormPane, GlobalProperties globalProperties) {
        this.hostPane = hostPane;
        this.hostFormPane = hostFormPane;
        this.globalProperties = globalProperties;
    }

    public void deleteSelectedHosts() {
        globalProperties.getSelectedHostPanelListProperty().get().forEach(hostPane::removeHostPanel);
        hostPane.refresh();
    }

    public void deleteSelectedHosts(HostPanel sourcePanel) {
        ObservableList<HostPanel> selectedHosts = globalProperties.getSelectedHostPanelListProperty().get();
        if (!selectedHosts.isEmpty()) {
            deleteSelectedHosts();
        } else {
            hostPane.removeHostPanel(sourcePanel);
            hostPane.refresh();
        }
    }

    public void editSelectedHost(HostPanel sourcePanel) {
        hostFormPane.showFormPane(sourcePanel);
    }

}
