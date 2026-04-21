package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.application.ChangeListenerRegistry;
import com.snjdigitalsolutions.lablensfx.nodes.tableview.SingleColumnConfigurationPathTableView;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.model.ComputeResourceModel;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.state.ShowIpAddressState;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatusIndicator;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.Getter;
import org.controlsfx.control.ToggleSwitch;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Scope("prototype")
public class HostPanelLarge extends GridPane implements IpSortable {

    @FXML
    private HBox hostHBox;
    @FXML
    private Label hostNameLabel;
    @FXML
    private Label ipAddressLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label sshPortLabel;
    @FXML
    private ToggleSwitch sshCommToggle;
    @FXML
    private HBox configPathHBox;
    private final BooleanProperty sshToggleValue = new SimpleBooleanProperty(true);
    @Getter
    private ComputeResourceModel resourceModel;
    @Getter
    private final SshStatusIndicator statusIndicator;
    @Getter
    private Long computeResourceId;
    private final HostManagementService hostManagementService;
    private final ObjectProvider<SingleColumnConfigurationPathTableView> singleColumnConfigurationPathTableObjectProvider;
    private final ChangeListenerRegistry changeListenerRegistry;
    private final ComputeResourceState computeResourceState;
    private final ShowIpAddressState showIpAddressState;
    private SingleColumnConfigurationPathTableView table;

    public HostPanelLarge(@Value("classpath:/fxml/HostPanelLarge.fxml") Resource fxml,
                          SshStatusIndicator statusIndicator,
                          HostManagementService hostManagementService,
                          ShowIpAddressState showIpAddressState,
                          ObjectProvider<SingleColumnConfigurationPathTableView> singleColumnConfigurationPathTableObjectProvider,
                          ChangeListenerRegistry changeListenerRegistry,
                          ComputeResourceState computeResourceState,
                          ShowIpAddressState showIpAddressState1
    ) {
        this.statusIndicator = statusIndicator;
        this.hostManagementService = hostManagementService;
        this.singleColumnConfigurationPathTableObjectProvider = singleColumnConfigurationPathTableObjectProvider;
        this.changeListenerRegistry = changeListenerRegistry;
        this.computeResourceState = computeResourceState;
        this.showIpAddressState = showIpAddressState1;
        NodeLoader.load(fxml, this);
    }

    public void performInitialization(Long computeResourceId) {
        this.computeResourceId = computeResourceId;
        hostHBox.getChildren().addFirst(statusIndicator);
        sshCommToggle.selectedProperty().bindBidirectional(sshToggleValue);
        table = singleColumnConfigurationPathTableObjectProvider.getObject();
        table.performIntialization();
        configPathHBox.getChildren().add(table);
        HBox.setHgrow(table, Priority.ALWAYS);

        loadConfigurationPaths();
        computeResourceState.getComputeResourcesMap()
                .addListener((MapChangeListener<Long, ComputeResource>) change -> {
                    if (change.wasAdded() && change.wasRemoved()) {
                        loadConfigurationPaths();
                    }
                });
    }

    private void loadConfigurationPaths() {
        Optional<ComputeResource> optionalResource = hostManagementService.getComputerResourceById(computeResourceId);
        optionalResource.ifPresent(computeResource -> table.setConfigurationPaths(computeResource.getConfigurationPaths()));
    }

    public void addToggleListener() {
        ChangeListener<Boolean> listener = (obj, oldVal, newVal) -> {
            long setValue = 0L;
            if (newVal) {
                setValue = 1L;
            }
            hostManagementService.setResourceSshCommValue(computeResourceId, setValue);
            if (setValue == 1){
                hostManagementService.verifyHostSshStatus(computeResourceId);
            } else {
                if (this.statusIndicator.getHostSshStatus().equals(SshStatus.ONLINE)){
                    hostManagementService.changeHostSshStatusToUnknown(this, true);
                } else {
                    hostManagementService.changeHostSshStatusToUnknown(this, false);
                }
            }
        };
        changeListenerRegistry.add(this, sshCommToggle.selectedProperty(), listener);
    }

    @Override
    public String getIpAddress() {
        return resourceModel.getIpAddress();
    }


    public void setResourceModel(ComputeResourceModel resourceModel) {
        this.resourceModel = resourceModel;
        if (hostNameLabel.textProperty().isBound()){
            hostNameLabel.textProperty().unbind();
        }
        hostNameLabel.textProperty().bind(resourceModel.hostNameProperty());
        if (ipAddressLabel.textProperty().isBound()){
            ipAddressLabel.textProperty().unbind();
        }
        if (showIpAddressState.isShowIpProperty()) {
            ipAddressLabel.textProperty().bind(resourceModel.ipAddressProperty());
        } else {
            ipAddressLabel.textProperty()
                    .setValue("xxx.xxx.xxx.xxx");
        }

        if (descriptionLabel.textProperty().isBound()){
            descriptionLabel.textProperty().unbind();
        }
        descriptionLabel.textProperty().bind(resourceModel.descriptionProperty());
        if (sshPortLabel.textProperty().isBound()){
          sshPortLabel.textProperty().unbind();
        }
        sshPortLabel.textProperty().bind(resourceModel.sshPortProperty().asString());
        sshToggleValue.setValue(resourceModel.getSshCommunicate() == 1);
    }
}
