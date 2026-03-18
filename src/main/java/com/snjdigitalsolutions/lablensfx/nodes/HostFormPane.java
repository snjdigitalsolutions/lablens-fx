package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.ComputeResourceProperties;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.springbootutilityfx.node.CloseableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private Button cancelButton;
    @FXML
    private Button submitButton;

    private final NodeUtility nodeUtility;
    private final AlertUtility alertUtility;
    private final IpAddressUtility ipAddressUtility;
    private final HostManagementService hostManagementService;
    private final ComputeResourceProperties computeResourceProperties;

    public HostFormPane(@Value("classpath:/fxml/HostFormPane.fxml") Resource fxml,
                        NodeUtility nodeUtility,
                        AlertUtility alertUtility,
                        IpAddressUtility ipAddressUtility,
                        HostManagementService hostManagementService,
                        ComputeResourceProperties computeResourceProperties) {
        this.nodeUtility = nodeUtility;
        this.alertUtility = alertUtility;
        this.hostManagementService = hostManagementService;
        this.ipAddressUtility = ipAddressUtility;
        this.computeResourceProperties = computeResourceProperties;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        cancelButton.setOnAction(this::close);
        submitButton.setOnAction(event -> {
            if (performFormValidation()) {
                if (computeResourceProperties.getComputerResourceBeingEdited() == null){
                    ComputeResource resource = new ComputeResource();
                    setValuesOnResource(resource);
                    hostManagementService.addComputeResource(resource);
                    this.close(event);
                }
                else {
                    ComputeResource resource = computeResourceProperties.getComputerResourceBeingEdited();
                    setValuesOnResource(resource);
                    hostManagementService.updateComputeResource(resource);
                    this.close(event);
                }
            }
        });
        computeResourceProperties.computerResourceBeingEditedProperty().addListener((obj, oldVal, newVal) -> {
            if (newVal != null) {
                showPane(newVal);
            }
        });

    }

    private void  setValuesOnResource(ComputeResource resource) {
        resource.setHostName(hostNameTextField.getText());
        resource.setIpAddress(ipaddressTextField.getText());
        resource.setOperatingSystem(operatingSystemTextField.getText());
        if (!descriptionTextArea.getText().isEmpty()) {
            resource.setDescription(descriptionTextArea.getText());
        }
    }

    public boolean performFormValidation() {
        boolean valid = false;
        if (!hostNameTextField.getText().isEmpty() && !ipaddressTextField.getText().isEmpty() && !operatingSystemTextField.getText().isEmpty()) {
            if (ipAddressUtility.isValidIpAddress(ipaddressTextField.getText())) {
                valid = true;
            } else {
                alertUtility.warningAlert("Invalid Address", "The IP address entered is invalid.");
            }
        }
        return valid;
    }

    private void clearForm() {
        hostNameTextField.clear();
        ipaddressTextField.clear();
        operatingSystemTextField.clear();
        descriptionTextArea.clear();
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
