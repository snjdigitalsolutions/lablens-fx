package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.state.ShowIpAddressState;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.utility.IpComparator;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    private final BooleanProperty performRefresh = new SimpleBooleanProperty(false);

    private final ComputeResourceState computeResourceState;
    private final HostManagementService hostManagementService;
    private final IpComparator ipComparator;
    private final ShowIpAddressState showIpAddressState;

    public HostPane(@Value("classpath:/fxml/HostPane.fxml") Resource fxml, ComputeResourceState computeResourceState, HostManagementService hostManagementService, IpComparator ipComparator, ShowIpAddressState showIpAddressState) {
        this.computeResourceState = computeResourceState;
        this.hostManagementService = hostManagementService;
        this.ipComparator = ipComparator;
        this.showIpAddressState = showIpAddressState;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        performRefresh.bind(computeResourceState.computeResourcesLoadedProperty());
        performRefresh.addListener((obj, oldVal, newVal) -> {
            if (newVal) {
                refresh();
            }
        });
        panelVBox.setAlignment(Pos.CENTER_LEFT);
        computeResourceState.computeResourcesMapProperty()
                .addListener((MapChangeListener<Long, ComputeResource>) change -> {
                    if (change.wasAdded() && !change.wasRemoved()) {
                        if (performRefresh.getValue()) {
                            refresh();
                        }
                    } else if (!change.wasAdded() && change.wasRemoved()) {
                        if (performRefresh.getValue()) {
                            refresh();
                        }
                    }
                });
        showIpAddressState.showIpPropertyProperty()
                .addListener((obj, oldVal, newVal) -> {
                    refresh();
                });
    }

    private void refresh() {
        panelVBox.getChildren()
                .clear();
        List<HostPanel> hostPanels = hostManagementService.getHostPanels();
        hostPanels.sort(ipComparator);
        panelVBox.getChildren()
                .addAll(hostPanels);
    }
}
