package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.state.*;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import jakarta.annotation.PostConstruct;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


@Component
@Scope("prototype")
public class HostPanel extends GridPane implements SpringInitializableNode, IpSortable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostPanel.class);

    @FXML
    private Label hostNameLabel;
    private final StringProperty hostname = new SimpleStringProperty();
    @FXML
    private Label ipAddressLabel;
    private final StringProperty ipAddress = new SimpleStringProperty();
    @FXML
    private Label sshPortLabel;
    private final IntegerProperty sshPort = new SimpleIntegerProperty(22);
    @FXML
    private FontAwesomeIconView deleteIcon;
    @FXML
    private FontAwesomeIconView pencilIcon;
    @Getter
    @Setter
    private ComputeResource computeResource;

    private final StatusBarState statusBarState;
    private final HostManagementService hostManagementService;
    private final ShowIpAddressState showIpAddressState;
    private final AlertUtility alertUtility;
    private final ComputeResourceState computeResourceState;
    private final SelectedViewState selectedViewState;
    private final ConfigurationPane configurationPane;
    private final MenuItemSelectionState menuItemSelectionState;

    public HostPanel(@Value("classpath:/fxml/HostPanel.fxml") Resource fxml, StatusBarState statusBarState, HostPane hostPane, HostManagementService hostManagementService, ShowIpAddressState showIpAddressState, AlertUtility alertUtility, ComputeResourceState computeResourceState, SelectedViewState selectedViewState, ConfigurationPane configurationPane, MenuItemSelectionState menuItemSelectionState) {
        this.statusBarState = statusBarState;
        this.hostManagementService = hostManagementService;
        this.showIpAddressState = showIpAddressState;
        this.alertUtility = alertUtility;
        this.computeResourceState = computeResourceState;
        this.selectedViewState = selectedViewState;
        this.configurationPane = configurationPane;
        this.menuItemSelectionState = menuItemSelectionState;
        NodeLoader.load(fxml, this);
    }

    @PostConstruct
    @Override
    public void performIntialization() {
        addSelectedStyle(this);
        if (showIpAddressState.isShowIpProperty()) {
            ipAddressLabel.textProperty()
                    .bind(ipAddress);
        } else {
            ipAddressLabel.textProperty().unbind();
            ipAddressLabel.textProperty()
                    .setValue("xxx.xxx.xxx.xxx");
        }
        hostNameLabel.textProperty()
                .bind(hostname);
        sshPortLabel.textProperty()
                .bind(sshPort.asString());
    }

    private void addSelectedStyle(Node node) {
        node.setOnMouseClicked(event -> {
            if (node.getStyleClass()
                    .contains("host-panel-selected")) {
                node.getStyleClass()
                        .remove("host-panel-selected");
                int currentValue = statusBarState.numberOfSelectedHostsProperty()
                        .getValue();
                currentValue--;
                LOGGER.debug("Panel deselected - {}", currentValue);
                statusBarState.numberOfSelectedHostsProperty()
                        .set(currentValue);
                statusBarState.selectedHostPanelListProperty()
                        .remove(this);
                computeResourceState.getSelectedResources()
                        .remove(this.computeResource);
            } else {
                if (statusBarState.numberOfSelectedHostsProperty()
                        .intValue() >= 1 && selectedViewState.getSelectedView() != ApplicationView.DASHBOARD) {
                    AtomicBoolean yesResponse = new AtomicBoolean(false);
                    if (menuItemSelectionState.isConfirmConfigurationChangeSelection()){
                        alertUtility.confirmAlert("Multiple Selections", "Do you wish to return to the dashboard?", () -> {
                            yesResponse.set(true);
                        });
                    }

                    if (yesResponse.get()) {
                        addedSelectionStyleToNode(node);
                    } else {
                        computeResourceState.getSelectedResources()
                                .getFirst()
                                .getHostPanel()
                                .getStyleClass()
                                .remove("host-panel-selected");
                        computeResourceState.getSelectedResources()
                                .clear();
                        computeResourceState.getSelectedResources()
                                .add(this.computeResource);
                        getStyleClass()
                                .add("host-panel-selected");
                        statusBarState.getSelectedHostPanelList().clear();
                        statusBarState.getSelectedHostPanelList().add(this);
                        configurationPane.loadExistingPaths();
                    }
                } else {
                    addedSelectionStyleToNode(node);
                }
            }
        });
        pencilIcon.setOnMouseClicked(event -> {
            hostManagementService.editSelectedHost(this);
            event.consume();
        });
        deleteIcon.setOnMouseClicked(event -> {
            if (!computeResourceState.getSelectedResources()
                    .isEmpty()){
                AtomicReference<HostPanel> reference = new AtomicReference<>(this);
                alertUtility.confirmAlert("Delete Hosts", "Are you sure you want to delete selected hosts?", () -> {
                    hostManagementService.deleteSelectedHosts(reference.get());
                });
            } else {
                alertUtility.warningAlert("No Selection", "No compute resources are selected");
            }
            event.consume();
        });

    }

    private void addedSelectionStyleToNode(Node node) {
        node.getStyleClass()
                .add("host-panel-selected");
        int currentValue = statusBarState.numberOfSelectedHostsProperty()
                .getValue();
        currentValue++;
        LOGGER.debug("Panel selected - {}", currentValue);
        statusBarState.numberOfSelectedHostsProperty()
                .set(currentValue);
        statusBarState.selectedHostPanelListProperty()
                .add(this);
        computeResourceState.getSelectedResources()
                .add(this.computeResource);
    }

    @Override
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
