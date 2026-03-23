package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.ComputeResourceProperties;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.service.SshService;
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

    private final NodeUtility nodeUtility;
    private final AlertUtility alertUtility;
    private final IpAddressUtility ipAddressUtility;
    private final HostManagementService hostManagementService;
    private final ComputeResourceProperties computeResourceProperties;
    private final SshService sshService;
    private final EtcOsReleaseParser osReleaseParser;

    public HostFormPane(@Value("classpath:/fxml/HostFormPane.fxml") Resource fxml, NodeUtility nodeUtility, AlertUtility alertUtility, IpAddressUtility ipAddressUtility, HostManagementService hostManagementService, ComputeResourceProperties computeResourceProperties, SshService sshService, EtcOsReleaseParser osReleaseParser) {
        this.nodeUtility = nodeUtility;
        this.alertUtility = alertUtility;
        this.hostManagementService = hostManagementService;
        this.ipAddressUtility = ipAddressUtility;
        this.computeResourceProperties = computeResourceProperties;
        this.sshService = sshService;
        this.osReleaseParser = osReleaseParser;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        cancelButton.setOnAction(this::close);
        submitButton.setOnAction(event -> {
            if (!autoCheckBox.isSelected() && performFormValidation(autoCheckBox.isSelected())) {
                if (computeResourceProperties.getComputerResourceBeingEdited() == null) {
                    ComputeResource resource = new ComputeResource();
                    setValuesOnResource(resource);
                    resource.setSshCommunicate(0L);
                    hostManagementService.addComputeResource(resource);
                    this.close(event);
                } else {
                    ComputeResource resource = computeResourceProperties.getComputerResourceBeingEdited();
                    setValuesOnResource(resource);
                    hostManagementService.updateComputeResource(resource);
                    this.close(event);
                }
            } else if (autoCheckBox.isSelected() && performFormValidation(autoCheckBox.isSelected())) {
                try {
                    //TODO get rid of username hard code
                    String osReleaseText = sshService.executeCommand(ipaddressTextField.getText(), Integer.parseInt(sshPortTextField.getText()), "cat /etc/os-release");
                    String hostName = sshService.executeCommand(ipaddressTextField.getText(), Integer.parseInt(sshPortTextField.getText()),  "hostname");
                    String[] hostParts = new String[1];
                    if (!hostName.isEmpty()){
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
        computeResourceProperties.computerResourceBeingEditedProperty()
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

    private void setValuesOnResource(ComputeResource resource) {
        resource.setHostName(hostNameTextField.getText());
        resource.setIpAddress(ipaddressTextField.getText());
        resource.setOperatingSystem(operatingSystemTextField.getText());
        resource.setSshPort(Integer.parseInt(sshPortTextField.getText()));
        if (!descriptionTextArea.getText()
                .isEmpty()) {
            resource.setDescription(descriptionTextArea.getText());
        }
    }

    public boolean performFormValidation(boolean autoPopulate) {
        boolean valid = false;
        if (!autoPopulate) {
            if (!hostNameTextField.getText()
                    .isEmpty() && !ipaddressTextField.getText()
                    .isEmpty() && !operatingSystemTextField.getText()
                    .isEmpty() && !sshPortTextField.getText()
                    .isEmpty()) {
                if (ipAddressUtility.isValidIpAddress(ipaddressTextField.getText())) {
                    valid = true;
                } else {
                    alertUtility.warningAlert("Invalid Address", "The IP address entered is invalid.");
                }
            } else {
                alertUtility.warningAlert("Populate Fields", "Only the description is optional.");
            }
        } else {
            if (!ipaddressTextField.getText()
                    .isEmpty() && !sshPortTextField.getText()
                    .isEmpty()) {
                valid = true;
            } else {
                alertUtility.warningAlert("Populate Fields", "IP address and port fields are required.");
            }
        }
        return valid;
    }

    private void clearForm() {
        hostNameTextField.clear();
        ipaddressTextField.clear();
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
        ipaddressTextField.setText(resource.getIpAddress());
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
