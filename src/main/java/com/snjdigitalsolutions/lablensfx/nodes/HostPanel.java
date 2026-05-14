package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.model.ComputeResourceModel;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.service.HostPanelStylingService;
import com.snjdigitalsolutions.lablensfx.service.ViewService;
import com.snjdigitalsolutions.lablensfx.service.node.HostPanelService;
import com.snjdigitalsolutions.lablensfx.state.MenuItemSelectionState;
import com.snjdigitalsolutions.lablensfx.state.ShowIpAddressState;
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
    @FXML
    private Label ipAddressLabel;
    @FXML
    private Label sshPortLabel;
    @FXML
    private FontAwesomeIconView deleteIcon;
    @FXML
    private FontAwesomeIconView pencilIcon;

    @Getter
    private ComputeResourceModel resourceModel;

    private final HostManagementService hostManagementService;
    private final ShowIpAddressState showIpAddressState;
    private final AlertUtility alertUtility;
    private final MenuItemSelectionState menuItemSelectionState;
    private final ViewService viewService;
    private final HostPanelStylingService hostPanelStylingService;
    private final HostPanelService hostPanelService;

    private boolean selected = false;

    /**
     * Creates a host panel for the given compute resource model.
     */
    public HostPanel(@Value("classpath:/fxml/HostPanel.fxml") Resource fxml,
                     HostManagementService hostManagementService,
                     ShowIpAddressState showIpAddressState,
                     AlertUtility alertUtility,
                     MenuItemSelectionState menuItemSelectionState,
                     ViewService viewService,
                     HostPanelStylingService hostPanelStylingService,
                     HostPanelService hostPanelService
    )
    {
        this.hostManagementService = hostManagementService;
        this.showIpAddressState = showIpAddressState;
        this.alertUtility = alertUtility;
        this.menuItemSelectionState = menuItemSelectionState;
        this.viewService = viewService;
        this.hostPanelStylingService = hostPanelStylingService;
        this.hostPanelService = hostPanelService;
        NodeLoader.load(fxml, this);
    }

    /**
     * Wires all property bindings, icons, and mouse-click handlers after construction.
     */
    @PostConstruct
    @Override
    public void performIntialization() {
        initializeMouseClickAction();
        initializePencilIconClick();
        initializeDeleteIconClick();
    }

    /**
     * Registers the delete-icon click handler to trigger host deletion.
     */
    private void initializeDeleteIconClick() {
        deleteIcon.setOnMouseClicked(event -> {
            if (hostManagementService.isComputeResourceSelected()) {
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

    /**
     * Registers the pencil-icon click handler to open the host edit form.
     */
    private void initializePencilIconClick() {
        pencilIcon.setOnMouseClicked(event -> {
            hostManagementService.editSelectedHost(this);
            event.consume();
        });
    }

    /**
     * Binds IP-address and hostname labels to the underlying model's observable properties.
     */
    private void bindProperties() {
        if (hostNameLabel.textProperty().isBound()){
            hostNameLabel.textProperty().unbind();
        }
        hostNameLabel.textProperty()
                .bind(resourceModel.hostNameProperty());
        if (sshPortLabel.textProperty().isBound()){
            hostNameLabel.textProperty().unbind();
        }
        sshPortLabel.textProperty()
                .bind(resourceModel.sshPortProperty().asString());

        if (ipAddressLabel.textProperty().isBound()){
            ipAddressLabel.textProperty().unbind();
        }
        if (showIpAddressState.isShowIpProperty()) {
            ipAddressLabel.textProperty()
                    .bind(resourceModel.ipAddressProperty());
        } else {
            ipAddressLabel.textProperty()
                    .unbind();
            ipAddressLabel.textProperty()
                    .setValue("xxx.xxx.xxx.xxx");
        }
    }

    /**
     * Registers the panel's mouse-click handler to change the selected host.
     */
    private void initializeMouseClickAction() {
        this.setOnMouseClicked(event -> {
            if (selected) { // HostPanel currently selected
                selected = false;
                hostPanelStylingService.removeSelectionStyle(this);
                hostManagementService.removeComputeResourceFromSelectedSources(this);
            } else { //When multiple hosts selected and user not on dashboard view

                LOGGER.debug("Multiple hosts being selected: " + hostManagementService.multipleHostsBeingSelected());
                if (hostManagementService.multipleHostsBeingSelected() && !viewService.dashboardSelected()) {
                    AtomicBoolean yesResponse = new AtomicBoolean(false);
                    if (menuItemSelectionState.isConfirmConfigurationChangeSelection()) {
                        alertUtility.confirmAlert("Multiple Selections", "Do you wish to return to the dashboard?", () -> {
                            yesResponse.set(true);
                        });
                    }
                    //When choosing to return to dashboard allow multiple host selections
                    if (yesResponse.get()) {
                        hostManagementService.addComputeResourceToSelectedSources(this);
                        hostPanelService.setHostPanelSelected(this);
                    } else {
                        hostManagementService.clearCurrentlySelectedHostAndAddNewlySelectedHost(this);
                        hostPanelService.changeSelectedHostPanel(this);
                    }
                } else {
                    hostManagementService.addComputeResourceToSelectedSources(this);
                    hostPanelService.setHostPanelSelected(this);
                }
            }
        });
    }

    /**
     * Visually marks this panel as selected or deselected.
     *
     * @param selected {@code true} to apply selection styling; {@code false} to remove it
     */
    public void setSelectionState(boolean selected) {
        this.selected = selected;
    }

    /**
     * Returns the IP address displayed on this host panel.
     *
     * @return the panel's IP address string
     */
    @Override
    public String getIpAddress() {
        return resourceModel.getIpAddress();
    }

    /**
     * Updates this panel to reflect the given compute resource model.
     *
     * @param resourceModel the model containing updated host data
     */
    public void setResourceModel(ComputeResourceModel resourceModel) {
        this.resourceModel = resourceModel;
        bindProperties();
    }
}
