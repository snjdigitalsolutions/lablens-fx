package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.springbootutilityfx.event.StageReadyEvent;
import com.snjdigitalsolutions.springbootutilityfx.node.CloseableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.StageNodeBuilder;
import jakarta.annotation.PostConstruct;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class HostFormPane extends AnchorPane implements SpringInitializableNode, CloseableNode {

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
    private final ComputeResourceRepository computeResourceRepository;
    private Runnable onSubmit;

    public HostFormPane(@Value("classpath:/fxml/HostFormPane.fxml") Resource fxml, NodeUtility nodeUtility, ComputeResourceRepository computeResourceRepository) {
        this.nodeUtility = nodeUtility;
        this.computeResourceRepository = computeResourceRepository;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        cancelButton.setOnAction(this::close);
        submitButton.setOnAction(submitEvent -> {
            if (performFormValidation()) {
                ComputeResource resource = new ComputeResource();
                resource.setHostName(hostNameTextField.getText());
                resource.setIpAddress(ipaddressTextField.getText());
                resource.setOperatingSystem(operatingSystemTextField.getText());
                if (!descriptionTextArea.getText().isEmpty()) {
                    resource.setDescription(descriptionTextArea.getText());
                }
                computeResourceRepository.save(resource);
                if (onSubmit != null) {
                    onSubmit.run();
                }
            }
            this.close(submitEvent);
        });
    }

    private boolean performFormValidation() {
       return !hostNameTextField.getText().isEmpty() && !ipaddressTextField.getText().isEmpty() && !operatingSystemTextField.getText().isEmpty();
    }

    @Override
    public void close(ActionEvent event) {
        nodeUtility.closeNode(event);
    }

    public void showFormPane() {
        StageNodeBuilder.builder()
            .setNode(this)
            .setModality(Modality.APPLICATION_MODAL)
            .buildAndShow();
    }

    public void setOnSubmit(Runnable onSubmit) {
        this.onSubmit = onSubmit;
    }


}
