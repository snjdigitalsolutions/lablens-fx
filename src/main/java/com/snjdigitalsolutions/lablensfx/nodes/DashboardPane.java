package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.properties.GlobalProperties;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class DashboardPane extends AnchorPane implements SpringInitializableNode {

    @FXML
    private HBox summaryPanelHBox;

    private final ObjectProvider<SummaryPanel> summaryPanelProvider;
    private final GlobalProperties globalProperties;

    public DashboardPane(@Value("classpath:/fxml/DashboardPane.fxml") Resource fxml, ObjectProvider<SummaryPanel> summaryPanelProvider, GlobalProperties globalProperties){
        this.summaryPanelProvider = summaryPanelProvider;
        this.globalProperties = globalProperties;
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

    }
}
