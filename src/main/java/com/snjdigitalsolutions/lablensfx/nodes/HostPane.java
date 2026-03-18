package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.GlobalProperties;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.IpAddressUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


@Component
public class HostPane extends AnchorPane implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostPane.class);

    @FXML
    private VBox panelVBox;

    private final GlobalProperties globalProperties;
    private final HostManagementService hostManagementService;

    public HostPane(@Value("classpath:/fxml/HostPane.fxml") Resource fxml,
                    GlobalProperties globalProperties,
                    HostManagementService hostManagementService) {
        this.globalProperties = globalProperties;
        this.hostManagementService = hostManagementService;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        panelVBox.setAlignment(Pos.CENTER_LEFT);
        globalProperties.computeResourcesMapProperty().addListener((MapChangeListener<Long, ComputeResource>) change -> {
            if (change.wasAdded()) {
                HostPanel panel = hostManagementService.createHostPanelForComputeResource(change.getValueAdded());
                change.getValueAdded().setHostPanel(panel);
                panelVBox.getChildren().add(panel);
            } else if (change.wasRemoved()) {
                refresh();
            }
        });
    }

    private void refresh() {
        panelVBox.getChildren().clear();
        panelVBox.getChildren().addAll(hostManagementService.getHostPanels());
    }
}
