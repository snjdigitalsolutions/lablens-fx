package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.ComputeResourceProperties;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.IpAddressUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DashboardPane extends AnchorPane implements SpringInitializableNode {

    @FXML
    private HBox summaryPanelHBox;
    @FXML
    private TilePane hostFlowPane;

    private final ObjectProvider<SummaryPanel> summaryPanelProvider;
    private final ObjectProvider<HostPanelLarge> hostPanelLargeProvider;
    private final ComputeResourceProperties computeResourceProperties;
    private final IpAddressUtility ipAddressUtility;

    public DashboardPane(@Value("classpath:/fxml/DashboardPane.fxml") Resource fxml,
                         ObjectProvider<SummaryPanel> summaryPanelProvider,
                         ObjectProvider<HostPanelLarge> hostPanelLargeProvider,
                         ComputeResourceProperties computeResourceProperties,
                         IpAddressUtility ipAddressUtility){
        this.summaryPanelProvider = summaryPanelProvider;
        this.hostPanelLargeProvider = hostPanelLargeProvider;
        this.computeResourceProperties = computeResourceProperties;
        this.ipAddressUtility = ipAddressUtility;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        SummaryPanel numberOfHostsPanel = summaryPanelProvider.getObject();
        numberOfHostsPanel.performIntialization();
        numberOfHostsPanel.setHeaderLabelText("Total Hosts");
        numberOfHostsPanel.setMoreInfoLabel("registered");
        HBox.setHgrow(numberOfHostsPanel, Priority.ALWAYS);
        computeResourceProperties.computeResourcesMapProperty().addListener((MapChangeListener<Long,ComputeResource>) change -> {
            numberOfHostsPanel.setCountLabel(Integer.toString(computeResourceProperties.getComputeResourcesMap().size()));
        });
        summaryPanelHBox.getChildren().add(numberOfHostsPanel);

        SummaryPanel numberOfHostsOnlinePanel = summaryPanelProvider.getObject();
        numberOfHostsOnlinePanel.performIntialization();
        numberOfHostsOnlinePanel.setHeaderLabelText("Hosts Online");
        numberOfHostsOnlinePanel.setMoreInfoLabel("reachable via SSH");
        numberOfHostsOnlinePanel.setCountLabelStyleClass("summary-panel-count-green");
        HBox.setHgrow(numberOfHostsOnlinePanel, Priority.ALWAYS);
        summaryPanelHBox.getChildren().add(numberOfHostsOnlinePanel);

        SummaryPanel numberOfConfigurationChangesPanel = summaryPanelProvider.getObject();
        numberOfConfigurationChangesPanel.performIntialization();
        numberOfConfigurationChangesPanel.setHeaderLabelText("Configuration Changes");
        numberOfConfigurationChangesPanel.setMoreInfoLabel("all hosts");
        numberOfConfigurationChangesPanel.setCountLabelStyleClass("summary-panel-count-orange");
        HBox.setHgrow(numberOfConfigurationChangesPanel, Priority.ALWAYS);
        summaryPanelHBox.getChildren().add(numberOfConfigurationChangesPanel);

        SummaryPanel numberOfLogErrorsPanel = summaryPanelProvider.getObject();
        numberOfLogErrorsPanel.performIntialization();
        numberOfLogErrorsPanel.setHeaderLabelText("Log Errors");
        numberOfLogErrorsPanel.setMoreInfoLabel("all hosts");
        numberOfLogErrorsPanel.setCountLabelStyleClass("summary-panel-count-red");
        HBox.setHgrow(numberOfLogErrorsPanel, Priority.ALWAYS);
        summaryPanelHBox.getChildren().add(numberOfLogErrorsPanel);

        computeResourceProperties.getComputeResourcesMap().addListener((MapChangeListener<Long,ComputeResource>) change -> {
            if (change.wasAdded() || change.wasRemoved()) {
                refresh();
            }
        });
    }

    public void refresh() {
        hostFlowPane.getChildren().clear();
        Map<String,HostPanelLarge> ipAddressToPanelMap = new HashMap<>();
        computeResourceProperties.getComputeResourcesMap().values().forEach(resource -> {
            HostPanelLarge panel = hostPanelLargeProvider.getObject();
            panel.performInitialization();
            panel.hostnameProperty().setValue(resource.getHostName());
            panel.ipAddressProperty().setValue(resource.getIpAddress());
            panel.descriptionProperty().setValue(resource.getDescription());
            panel.getStyleClass().add("host-panel");
            resource.setHostPanelLarge(panel);
            ipAddressToPanelMap.put(resource.getIpAddress(), panel);
        });

        ipAddressUtility.sortIpAddresses(new ArrayList<>(ipAddressToPanelMap.keySet())).forEach(ipAddress -> {
            hostFlowPane.getChildren().add(ipAddressToPanelMap.get(ipAddress));
        });

//        hostFlowPane.getChildren().forEach(node -> {
//            Platform.runLater(() -> {
//                double height = node.getBoundsInParent().getHeight();
//                if (globalProperties.getLargeHostPanelHeight() < height) {
//                    globalProperties.largeHostPanelHeightProperty().setValue(height);
//                }
//            });
//        });

    }
}
