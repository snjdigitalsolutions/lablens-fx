package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.StatusBarProperties;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import jakarta.annotation.PostConstruct;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;


@Component
@Scope("prototype")
public class HostPanel extends GridPane implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostPanel.class);

    @FXML
    private Label hostNameLabel;
    private final StringProperty hostname = new SimpleStringProperty();
    @FXML
    private Label ipAddressLabel;
    private final StringProperty ipAddress = new SimpleStringProperty();
    @FXML
    private FontAwesomeIconView deleteIcon;
    @FXML
    private FontAwesomeIconView pencilIcon;
    @Getter
    @Setter
    private ComputeResource computeResource;

    private final StatusBarProperties statusBarProperties;
    private final HostManagementService hostManagementService;
    private final AlertUtility alertUtility;

    public HostPanel(@Value("classpath:/fxml/HostPanel.fxml") Resource fxml,
                     StatusBarProperties statusBarProperties,
                     HostPane hostPane,
                     HostManagementService hostManagementService,
                     AlertUtility alertUtility){
        this.statusBarProperties = statusBarProperties;
        this.hostManagementService = hostManagementService;
        this.alertUtility = alertUtility;
        NodeLoader.load(fxml, this);
    }

    @PostConstruct
    @Override
    public void performIntialization() {
        addSelectedStyle(this);
        ipAddressLabel.textProperty().bind(ipAddressProperty());
        hostNameLabel.textProperty().bind(hostnameProperty());
    }

    private void addSelectedStyle(Node node) {
        node.setOnMouseClicked(event -> {
            if (node.getStyleClass().contains("host-panel-selected")) {
                node.getStyleClass().remove("host-panel-selected");
                int currentValue = statusBarProperties.numberOfSelectedHostsProperty().getValue();
                currentValue--;
                LOGGER.debug("Panel deselected - {}", currentValue);
                statusBarProperties.numberOfSelectedHostsProperty().set(currentValue);
                statusBarProperties.selectedHostPanelListProperty().remove(this);
            } else {
                node.getStyleClass().add("host-panel-selected");
                int currentValue = statusBarProperties.numberOfSelectedHostsProperty().getValue();
                currentValue++;
                LOGGER.debug("Panel selected - {}", currentValue);
                statusBarProperties.numberOfSelectedHostsProperty().set(currentValue);
                statusBarProperties.selectedHostPanelListProperty().add(this);
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

    public String getIpAddress() {
        return ipAddress.get();
    }

    public StringProperty ipAddressProperty() {
        return ipAddress;
    }

    public String getHostname() {
        return hostname.get();
    }

    public StringProperty hostnameProperty() {
        return hostname;
    }
}
