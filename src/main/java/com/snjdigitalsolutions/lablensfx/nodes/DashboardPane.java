package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.properties.GlobalProperties;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.IpAddressUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    private FlowPane hostFlowPane;

    private final ObjectProvider<SummaryPanel> summaryPanelProvider;
    private final ObjectProvider<HostPanelLarge> hostPanelLargeProvider;
    private final GlobalProperties globalProperties;
    private final IpAddressUtility ipAddressUtility;

    public DashboardPane(@Value("classpath:/fxml/DashboardPane.fxml") Resource fxml,
                         ObjectProvider<SummaryPanel> summaryPanelProvider,
                         ObjectProvider<HostPanelLarge> hostPanelLargeProvider,
                         GlobalProperties globalProperties,
                         IpAddressUtility ipAddressUtility){
        this.summaryPanelProvider = summaryPanelProvider;
        this.hostPanelLargeProvider = hostPanelLargeProvider;
        this.globalProperties = globalProperties;
        this.ipAddressUtility = ipAddressUtility;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        SummaryPanel numberOfHostsPanel = summaryPanelProvider.getObject();
        numberOfHostsPanel.performIntialization();
        numberOfHostsPanel.setHeaderLabelText("Total Hosts");
        numberOfHostsPanel.setMoreInfoLabel("registered");
        globalProperties.getNumberOfHostsProperty().addListener((obj, oldVal, newVal) -> {
            numberOfHostsPanel.setCountLabel(newVal.intValue());
        });
        HBox.setHgrow(numberOfHostsPanel, Priority.ALWAYS);
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

        globalProperties.getComputeResourcesLoadedProperty().addListener((obj, oldVal, newVal) -> {
            if (newVal){
                refresh();
            }
        });
    }

    public void refresh() {
        hostFlowPane.getChildren().clear();

        Map<String,HostPanelLarge> ipAddressToPanelMap = new HashMap<>();
        globalProperties.getComputeResourcesProperty().get().forEach(resource -> {
            HostPanelLarge panel = hostPanelLargeProvider.getObject();
            panel.getHostNameLabel().setText(resource.getHostName());
            panel.getIpAddressLabel().setText(resource.getIpAddress());
            panel.getStyleClass().add("host-panel");
            ipAddressToPanelMap.put(resource.getIpAddress(), panel);
        });

        ipAddressUtility.sortIpAddresses(new ArrayList<>(ipAddressToPanelMap.keySet())).forEach(ipAddress -> {
            hostFlowPane.getChildren().add(ipAddressToPanelMap.get(ipAddress));
        });

    }
}
