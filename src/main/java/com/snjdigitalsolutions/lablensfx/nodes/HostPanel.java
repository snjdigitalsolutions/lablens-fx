package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.properties.GlobalProperties;
import com.snjdigitalsolutions.lablensfx.properties.StatusBarProperties;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.springbootutilityfx.event.StageReadyEvent;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import jakarta.annotation.PostConstruct;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;


@Component
@Scope("prototype")
public class HostPanel extends GridPane implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostPanel.class);

    @FXML
    @Getter
    private Label hostNameLabel;
    @FXML
    @Getter
    private Label ipAddressLabel;
    @FXML
    private FontAwesomeIconView deleteIcon;
    @FXML
    private FontAwesomeIconView pencilIcon;
    @Getter
    @Setter
    private Long hostId;

    private final StatusBarProperties statusBarProperties;
    private final GlobalProperties globalProperties;
    private final HostManagementService hostManagementService;
    private final AlertUtility alertUtility;

    public HostPanel(@Value("classpath:/fxml/HostPanel.fxml") Resource fxml,
                     StatusBarProperties statusBarProperties,
                     GlobalProperties globalProperties,
                     HostPane hostPane,
                     HostManagementService hostManagementService, AlertUtility alertUtility){
        this.statusBarProperties = statusBarProperties;
        this.globalProperties = globalProperties;
        this.hostManagementService = hostManagementService;
        this.alertUtility = alertUtility;
        NodeLoader.load(fxml, this);
    }

    @PostConstruct
    @Override
    public void performIntialization() {
        addSelectedStyle(this);
    }

    private void addSelectedStyle(Node node) {
        node.setOnMouseClicked(event -> {
            if (node.getStyleClass().contains("host-panel-selected")) {
                node.getStyleClass().remove("host-panel-selected");
                int currentValue = statusBarProperties.numberOfSelectedHostsProperty().getValue();
                currentValue--;
                LOGGER.debug("Panel deselected - {}", currentValue);
                statusBarProperties.numberOfSelectedHostsProperty().set(currentValue);
                globalProperties.getSelectedHostPanelListProperty().remove(this);
            } else {
                node.getStyleClass().add("host-panel-selected");
                int currentValue = statusBarProperties.numberOfSelectedHostsProperty().getValue();
                currentValue++;
                LOGGER.debug("Panel selected - {}", currentValue);
                statusBarProperties.numberOfSelectedHostsProperty().set(currentValue);
                globalProperties.getSelectedHostPanelListProperty().add(this);
            }
        });
        pencilIcon.setOnMouseClicked(event -> {
            hostManagementService.editSelectedHost(this);
            event.consume();
        });
        deleteIcon.setOnMouseClicked(event -> {
            AtomicReference<HostPanel> reference = new AtomicReference<>(this);
            alertUtility.confirmAlert("Delete Hosts", "Are you sure you want to delete selected hosts?", () -> {
                hostManagementService.deleteSelectedHosts(reference.get());
            });
            event.consume();
        });

    }

}
