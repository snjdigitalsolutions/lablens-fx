package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
import com.snjdigitalsolutions.lablensfx.shapes.StatusIndicator;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.ToggleSwitch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

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
    private final BooleanProperty sshToggleValue = new SimpleBooleanProperty(true);
    @Getter
    private final StatusIndicator statusIndicator;

    @Getter
    @Setter
    private Long computeResourceId;
    private final HostManagementService hostManagementService;

    public HostPanelLarge(@Value("classpath:/fxml/HostPanelLarge.fxml") Resource fxml,
                          StatusIndicator statusIndicator,
                          HostManagementService hostManagementService) {
        this.statusIndicator = statusIndicator;
        this.hostManagementService = hostManagementService;
        NodeLoader.load(fxml, this);
    }

    public void performInitialization() {
        hostHBox.getChildren().addFirst(statusIndicator);
        hostNameLabel.textProperty().bind(hostname);
        ipAddressLabel.textProperty().bind(ipAddress);
        descriptionLabel.textProperty().bind(description);
        sshPortLabel.textProperty().bind(sshPort.asString());
        sshCommToggle.selectedProperty().bindBidirectional(sshToggleValue);
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
