package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.ComputeResourceProperties;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.utility.IpComparator;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.IpAddressUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

import java.util.List;


@Component
public class HostPane extends AnchorPane implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostPane.class);

    @FXML
    private VBox panelVBox;

    private BooleanProperty performRefresh = new SimpleBooleanProperty(false);

    private final ComputeResourceProperties computeResourceProperties;
    private final HostManagementService hostManagementService;
    private final IpComparator ipComparator;

    public HostPane(@Value("classpath:/fxml/HostPane.fxml") Resource fxml,
                    ComputeResourceProperties computeResourceProperties,
                    HostManagementService hostManagementService,
                    IpComparator ipComparator) {
        this.computeResourceProperties = computeResourceProperties;
        this.hostManagementService = hostManagementService;
        this.ipComparator = ipComparator;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        performRefresh.bind(computeResourceProperties.computeResourcesLoadedProperty());
        performRefresh.addListener((obj, oldVal, newVal) -> {
            if (newVal){
                refresh();
            }
        });
        panelVBox.setAlignment(Pos.CENTER_LEFT);
        computeResourceProperties.computeResourcesMapProperty().addListener((MapChangeListener<Long, ComputeResource>) change -> {
            if (performRefresh.getValue()){
                refresh();
            }
        });
    }

    private void refresh() {
        panelVBox.getChildren().clear();
        List<HostPanel> hostPanels = hostManagementService.getHostPanels();
        hostPanels.sort(ipComparator);
        panelVBox.getChildren().addAll(hostPanels);
    }
}
