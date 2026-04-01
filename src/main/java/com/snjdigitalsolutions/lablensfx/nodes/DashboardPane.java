package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.ComputeResourceProperties;
import com.snjdigitalsolutions.lablensfx.properties.IpAddressProperties;
import com.snjdigitalsolutions.lablensfx.properties.StatusBarProperties;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.List;
import java.util.Map;

@Component
public class DashboardPane extends AnchorPane implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardPane.class);
    @FXML
    private HBox summaryPanelHBox;
    @FXML
    private TilePane hostFlowPane;

    private BooleanProperty performRefresh = new SimpleBooleanProperty(false);

    private final ObjectProvider<SummaryPanel> summaryPanelProvider;
    private final ObjectProvider<HostPanelLarge> hostPanelLargeProvider;
    private final ComputeResourceProperties computeResourceProperties;
    private final IpAddressProperties ipAddressProperties;
    private final StatusBarProperties statusBarProperties;

    public DashboardPane(@Value("classpath:/fxml/DashboardPane.fxml") Resource fxml,
                         ObjectProvider<SummaryPanel> summaryPanelProvider,
                         ObjectProvider<HostPanelLarge> hostPanelLargeProvider,
                         ComputeResourceProperties computeResourceProperties,
                         IpAddressProperties ipAddressProperties,
                         StatusBarProperties statusBarProperties) {
        this.summaryPanelProvider = summaryPanelProvider;
        this.hostPanelLargeProvider = hostPanelLargeProvider;
        this.computeResourceProperties = computeResourceProperties;
        this.ipAddressProperties = ipAddressProperties;
        this.statusBarProperties = statusBarProperties;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        performRefresh.bind(computeResourceProperties.computeResourcesLoadedProperty());
        computeResourceProperties.computeResourcesLoadedProperty()
                .addListener((obj, oldVal, newVal) -> {
                    if (newVal) {
                        refresh();
                    }
                });
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
                        if (performRefresh.getValue()) {
                            refresh();
                        }
                    }
                });
        this.widthProperty()
                .addListener((obj, oldVal, newVal) -> {
                    hostFlowPane.setMaxWidth(newVal.doubleValue());
                });
        ipAddressProperties.showIpPropertyProperty()
                .addListener((obj, oldVal, newVal) -> {
                    refresh();
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
                            panel.setCountLabel(newVal.toString());
                        });
            }
        }
    }

    public void refresh() {
        LOGGER.debug("Refreshing dashboard");
        clearHostPanel();
        Map<String, HostPanelLarge> ipAddressToPanelMap = new HashMap<>();
        computeResourceProperties.getComputeResourcesMap()
                .values()
                .forEach(resource -> {
                    HostPanelLarge panel = hostPanelLargeProvider.getObject();
                    panel.performInitialization();
                    panel.hostnameProperty()
                            .setValue(resource.getHostName());
                    if (ipAddressProperties.isShowIpProperty()) {
                        panel.ipAddressProperty()
                                .setValue(resource.getIpAddress());
                    } else {
                        panel.ipAddressProperty()
                                .setValue("xxx.xxx.xxx.xxx");
                    }
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

        List<String> ipAddresses = new ArrayList<>(ipAddressToPanelMap.keySet());
        ipAddresses.sort(String::compareTo);
        ipAddresses.forEach(ip -> {
            hostFlowPane.getChildren()
                    .add(ipAddressToPanelMap.get(ip));
        });
    }

    private void clearHostPanel() {
        hostFlowPane.getChildren()
                .clear();
        statusBarProperties.numberOfSelectedHostsProperty().setValue(0);
    }
}
