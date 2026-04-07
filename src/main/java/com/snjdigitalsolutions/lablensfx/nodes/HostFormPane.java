package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.state.SettingState;
import com.snjdigitalsolutions.lablensfx.utility.EtcOsReleaseParser;
import com.snjdigitalsolutions.springbootutilityfx.node.CloseableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class HostFormPane extends AnchorPane implements SpringInitializableNode, CloseableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostFormPane.class);
    @FXML
    private TextField hostNameTextField;
    @FXML
    private TextField ipaddressTextField;
    @FXML
    private TextField operatingSystemTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private TextField sshPortTextField;
    @FXML
    private CheckBox autoCheckBox;
    @FXML
    private Button cancelButton;
    @FXML
    private Button submitButton;

    private String actualIpValue = "";

    private final NodeUtility nodeUtility;
    private final AlertUtility alertUtility;
    private final IpAddressUtility ipAddressUtility;
    private final HostManagementService hostManagementService;
    private final ComputeResourceState computeResourceState;
    private final SshService sshService;
    private final EtcOsReleaseParser osReleaseParser;
    private final SettingState settingState;

    public HostFormPane(@Value("classpath:/fxml/HostFormPane.fxml") Resource fxml,
                        NodeUtility nodeUtility,
                        AlertUtility alertUtility,
                        IpAddressUtility ipAddressUtility,
                        HostManagementService hostManagementService,
                        ComputeResourceState computeResourceState,
                        SshService sshService,
                        EtcOsReleaseParser osReleaseParser,
                        SettingState settingState
    )
    {
        this.nodeUtility = nodeUtility;
        this.alertUtility = alertUtility;
        this.hostManagementService = hostManagementService;
        this.ipAddressUtility = ipAddressUtility;
        this.computeResourceState = computeResourceState;
        this.sshService = sshService;
        this.osReleaseParser = osReleaseParser;
        this.settingState = settingState;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        ipaddressTextField.setTextFormatter(new TextFormatter<>(change -> {
            if (!settingState.isShowIPs()) {
                int start = change.getRangeStart();
                int end = change.getRangeEnd();
                actualIpValue = actualIpValue.substring(0, start) + change.getText() + actualIpValue.substring(end);
                change.setText(change.getText().replaceAll("[0-9]", "x"));
            }
            return change;
        }));
        settingState.showIPsProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                actualIpValue = ipaddressTextField.getText();
                ipaddressTextField.setText(actualIpValue.replaceAll("[0-9]", "x"));
            } else {
                ipaddressTextField.setText(actualIpValue);
            }
        });
        cancelButton.setOnAction(this::close);
        submitButton.setOnAction(event -> {
            if (!autoCheckBox.isSelected() && performFormValidation(autoCheckBox.isSelected())) {
                if (computeResourceState.getComputerResourceBeingEdited() == null) {
                    ComputeResource resource = new ComputeResource();
                    setValuesOnResource(resource, true);
                    hostManagementService.addComputeResource(resource);
                    this.close(event);
                } else {
                    ComputeResource resource = computeResourceState.getComputerResourceBeingEdited();
                    setValuesOnResource(resource, false);
                    hostManagementService.updateComputeResource(resource);
                    this.close(event);
                }
            } else if (autoCheckBox.isSelected() && performFormValidation(autoCheckBox.isSelected())) {
                try {
                    String osReleaseText = sshService.executeCommand(getIpAddress(), Integer.parseInt(sshPortTextField.getText()), "cat /etc/os-release");
                    String hostName = sshService.executeCommand(getIpAddress(), Integer.parseInt(sshPortTextField.getText()), "hostname");
                    String[] hostParts = new String[1];
                    if (!hostName.isEmpty()) {
                        hostParts = hostName.split("\\.");
                    }
                    hostNameTextField.setText(hostParts[0]);
                    operatingSystemTextField.setText(osReleaseParser.getPrettyName(osReleaseText));
                    autoCheckBox.setSelected(false);
                } catch (Exception e) {
                    //TODO create utility for these exceptions
                    LOGGER.warn("Exception thrown for...");
                }
            }
        });
        computeResourceState.computerResourceBeingEditedProperty()
                .addListener((obj, oldVal, newVal) -> {
                    if (newVal != null) {
                        showPane(newVal);
                    }
                });
        autoCheckBox.selectedProperty()
                .addListener((obj, oldVal, newVal) -> {
                    descriptionTextArea.disableProperty()
                            .setValue(newVal);
                    hostNameTextField.disableProperty()
                            .setValue(newVal);
                    operatingSystemTextField.disableProperty()
                            .setValue(newVal);
                    if (newVal) {
                        ipaddressTextField.requestFocus();
                    } else {
                        hostNameTextField.requestFocus();
                    }
                });
    }

    private void setValuesOnResource(ComputeResource resource,
                                     boolean newResource
    )
    {
        resource.setHostName(hostNameTextField.getText());
        resource.setIpAddress(getIpAddress());
        resource.setOperatingSystem(operatingSystemTextField.getText());
        resource.setSshPort(Integer.parseInt(sshPortTextField.getText()));
        if (!descriptionTextArea.getText()
                .isEmpty()) {
            resource.setDescription(descriptionTextArea.getText());
        }
        if (newResource) {
            resource.setSshCommunicate(0L);
        }
    }

    public boolean performFormValidation(boolean autoPopulate) {
        boolean valid = false;
        if (!autoPopulate) {
            if (!hostNameTextField.getText()
                    .isEmpty() && !getIpAddress()
                    .isEmpty() && !operatingSystemTextField.getText()
                    .isEmpty() && !sshPortTextField.getText()
                    .isEmpty()) {
                if (ipAddressUtility.isValidIpAddress(getIpAddress())) {
                    valid = true;
                } else {
                    alertUtility.warningAlert("Invalid Address", "The IP address entered is invalid.");
                }
            } else {
                alertUtility.warningAlert("Populate Fields", "Only the description is optional.");
            }
        } else {
            if (!getIpAddress()
                    .isEmpty() && !sshPortTextField.getText()
                    .isEmpty()) {
                valid = true;
            } else {
                alertUtility.warningAlert("Populate Fields", "IP address and port fields are required.");
            }
        }
        return valid;
    }

    private String getIpAddress() {
        return !settingState.isShowIPs() ? actualIpValue : ipaddressTextField.getText();
    }

    private void clearForm() {
        hostNameTextField.clear();
        ipaddressTextField.clear();
        actualIpValue = "";
        operatingSystemTextField.clear();
        descriptionTextArea.clear();
        sshPortTextField.clear();
    }

    @Override
    public void close(ActionEvent event) {
        nodeUtility.closeNode(event);
    }

    public void showPane() {
        clearForm();
        makeFormVisible("Add Host");
    }

    public void showPane(ComputeResource resource) {
        hostNameTextField.setText(resource.getHostName());
        if (!settingState.isShowIPs()) {
            actualIpValue = resource.getIpAddress();
            ipaddressTextField.setText(actualIpValue.replaceAll("[0-9]", "x"));
        } else {
            ipaddressTextField.setText(resource.getIpAddress());
        }
        operatingSystemTextField.setText(resource.getOperatingSystem());
        descriptionTextArea.setText(resource.getDescription());
        if (resource.getSshPort() == null) {
            sshPortTextField.setText("0");
        } else {
            sshPortTextField.setText(resource.getSshPort()
                                             .toString());
        }
        makeFormVisible("Edit Host");
    }

    private void makeFormVisible(String title) {
        StageNodeBuilder.builder()
                .setNode(this)
                .setModality(Modality.APPLICATION_MODAL)
                .setTitle(title)
                .buildAndShow();
    }
}
