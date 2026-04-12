package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.application.ChangeListenerRegistry;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.state.ShowIpAddressState;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatusIndicator;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.beans.property.*;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.Getter;
import lombok.Setter;
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
    private final StringProperty hostname = new SimpleStringProperty();
    @FXML
    private Label ipAddressLabel;
    private final StringProperty ipAddress = new SimpleStringProperty();
    @FXML
    private Label descriptionLabel;
    private final StringProperty description = new SimpleStringProperty();
    @FXML
    private Label sshPortLabel;
    private final IntegerProperty sshPort = new SimpleIntegerProperty(22);
    @FXML
    private ToggleSwitch sshCommToggle;
    @FXML
    private HBox configPathHBox;
    private final BooleanProperty sshToggleValue = new SimpleBooleanProperty(true);
    @Getter
    private final SshStatusIndicator statusIndicator;
    @Getter
    private Long computeResourceId;
    private final HostManagementService hostManagementService;
    private final ObjectProvider<SingleColumnConfigurationPathTable> singleColumnConfigurationPathTableObjectProvider;
    private final ChangeListenerRegistry changeListenerRegistry;
    private final ComputeResourceState computeResourceState;
    private SingleColumnConfigurationPathTable table;

    public HostPanelLarge(@Value("classpath:/fxml/HostPanelLarge.fxml") Resource fxml,
                          SshStatusIndicator statusIndicator,
                          HostManagementService hostManagementService,
                          ShowIpAddressState showIpAddressState,
                          ObjectProvider<SingleColumnConfigurationPathTable> singleColumnConfigurationPathTableObjectProvider,
                          ChangeListenerRegistry changeListenerRegistry,
                          ComputeResourceState computeResourceState
    ) {
        this.statusIndicator = statusIndicator;
        this.hostManagementService = hostManagementService;
        this.singleColumnConfigurationPathTableObjectProvider = singleColumnConfigurationPathTableObjectProvider;
        this.changeListenerRegistry = changeListenerRegistry;
        this.computeResourceState = computeResourceState;
        NodeLoader.load(fxml, this);
    }

    public void performInitialization(Long computeResourceId) {
        this.computeResourceId = computeResourceId;
        hostHBox.getChildren().addFirst(statusIndicator);
        hostNameLabel.textProperty().bind(hostname);
        ipAddressLabel.textProperty().bind(ipAddress);
        descriptionLabel.textProperty().bind(description);
        sshPortLabel.textProperty().bind(sshPort.asString());
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
        sshToggleValueProperty().addListener((obj, oldVal, newVal) -> {
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
        });
    }

    public String getHostname() {
        return hostname.get();
    }

    public StringProperty hostnameProperty() {
        return hostname;
    }

    @Override
    public String getIpAddress() {
        return ipAddress.get();
    }

    public StringProperty ipAddressProperty() {
        return ipAddress;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public int getSshPort() {
        return sshPort.get();
    }

    public IntegerProperty sshPortProperty() {
        return sshPort;
    }

    public boolean isSshToggleValue() {
        return sshToggleValue.get();
    }

    public BooleanProperty sshToggleValueProperty() {
        return sshToggleValue;
    }
}
