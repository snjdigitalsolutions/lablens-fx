package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.ComputeResourceProperties;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.IpAddressUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class DashboardPane extends AnchorPane implements SpringInitializableNode {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardPane.class);
    @FXML
    private HBox summaryPanelHBox;
    @FXML
    private TilePane hostFlowPane;

    private final ObjectProvider<SummaryPanel> summaryPanelProvider;
    private final ObjectProvider<HostPanelLarge> hostPanelLargeProvider;
    private final ComputeResourceProperties computeResourceProperties;
    private final IpAddressUtility ipAddressUtility;

    public DashboardPane(@Value("classpath:/fxml/DashboardPane.fxml") Resource fxml, ObjectProvider<SummaryPanel> summaryPanelProvider, ObjectProvider<HostPanelLarge> hostPanelLargeProvider, ComputeResourceProperties computeResourceProperties, IpAddressUtility ipAddressUtility) {
        this.summaryPanelProvider = summaryPanelProvider;
        this.hostPanelLargeProvider = hostPanelLargeProvider;
        this.computeResourceProperties = computeResourceProperties;
        this.ipAddressUtility = ipAddressUtility;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        summaryPanelHBox.getChildren()
                .add(createSummaryPanel(SummaryPanelType.NUM_HOSTS));
        summaryPanelHBox.getChildren()
                .add(createSummaryPanel(SummaryPanelType.NUM_ONLINE));
        summaryPanelHBox.getChildren()
                .add(createSummaryPanel(SummaryPanelType.NUM_CONFIG_CHANGES));
        summaryPanelHBox.getChildren()
                .add(createSummaryPanel(SummaryPanelType.NUM_LOG_ERROR));
        computeResourceProperties.getComputeResourcesMap()
                .addListener((MapChangeListener<Long, ComputeResource>) change -> {
                    if (change.wasAdded() || change.wasRemoved()) {
                        refresh();
                    }
                });
    }

    private SummaryPanel createSummaryPanel(SummaryPanelType type) {
        SummaryPanel panel = summaryPanelProvider.getObject();
        panel.performIntialization();
        panel.setHeaderLabelText(type.getHeader());
        panel.setMoreInfoLabel(type.getMoreInfo());
        if (!type.getCssClass()
                .isEmpty()) {
            panel.setCountLabelStyleClass(type.getCssClass());
        }
        HBox.setHgrow(panel, Priority.ALWAYS);
        addListenerForLabel(panel, type);
        return panel;
    }

    private void addListenerForLabel(SummaryPanel panel, SummaryPanelType type) {
        switch (type) {
            case NUM_HOSTS -> {
                computeResourceProperties.computeResourcesMapProperty()
                        .addListener((MapChangeListener<Long, ComputeResource>) change -> {
                            panel.setCountLabel(Integer.toString(computeResourceProperties.getComputeResourcesMap()
                                    .size()));
                        });
            }
            case NUM_ONLINE -> {
                computeResourceProperties.hostsOnlineProperty()
                        .addListener((obj, oldVal, newVal) -> {
                            System.out.println("firing!");
                            panel.setCountLabel(newVal.toString());
                        });
            }
        }
    }

    public void refresh() {
        LOGGER.debug("Refreshing dashboard");
        hostFlowPane.getChildren()
                .clear();
        Map<String, HostPanelLarge> ipAddressToPanelMap = new HashMap<>();
        computeResourceProperties.getComputeResourcesMap()
                .values()
                .forEach(resource -> {
                    //TODO the state of the ssh connection needs to be outside the panel because of the refresh
                    HostPanelLarge panel = hostPanelLargeProvider.getObject();
                    panel.performInitialization();
                    panel.hostnameProperty()
                            .setValue(resource.getHostName());
                    panel.ipAddressProperty()
                            .setValue(resource.getIpAddress());
                    panel.descriptionProperty()
                            .setValue(resource.getDescription());
                    panel.sshPortProperty()
                            .setValue(resource.getSshPort());
                    panel.sshToggleValueProperty()
                            .setValue(resource.getSshCommunicate() == 1);
                    panel.setComputeResourceId(resource.getId());
                    panel.addToggleListener();
                    panel.getStyleClass()
                            .add("host-panel");
                    if (computeResourceProperties.getComputeResourceOnlineStatusMap()
                            .containsKey(resource.getId())) {
                        panel.getStatusIndicator()
                                .hostSshStatusProperty()
                                .setValue(computeResourceProperties.getComputeResourceOnlineStatusMap()
                                        .get(resource.getId()));
                    }
                    resource.setHostPanelLarge(panel);
                    ipAddressToPanelMap.put(resource.getIpAddress(), panel);
                });

        ipAddressUtility.sortIpAddresses(new ArrayList<>(ipAddressToPanelMap.keySet()))
                .forEach(ipAddress -> {
                    hostFlowPane.getChildren()
                            .add(ipAddressToPanelMap.get(ipAddress));
                });
    }
}
