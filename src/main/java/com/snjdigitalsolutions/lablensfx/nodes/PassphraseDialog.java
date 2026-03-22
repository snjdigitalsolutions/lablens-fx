package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.properties.SshProperties;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class PassphraseDialog extends GridPane implements SpringInitializableNode {

    @FXML
    private TextField userNamerTextField;
    @FXML
    private PasswordField passphrasePasswordField;
    @FXML
    private CheckBox noPassphraseCheckbox;
    @FXML
    private Button cancelButton;
    @FXML
    private Button submitButton;

    private final NodeUtility nodeUtility;
    private final SshProperties sshProperties;
    private final AlertUtility alertUtility;
    @Setter
    private Runnable postDialogAction;

    public PassphraseDialog(@Value("classpath:/fxml/PassphraseDialog.fxml") Resource fxml,
                            NodeUtility nodeUtility,
                            SshProperties sshProperties,
                            AlertUtility alertUtility) {
        this.nodeUtility = nodeUtility;
        this.sshProperties = sshProperties;
        this.alertUtility = alertUtility;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        cancelButton.setOnAction(nodeUtility::closeNode);
        submitButton.setOnAction(event -> {
            if (validateTextField() && postDialogAction != null) {
                sshProperties.passPhraseProperty()
                        .setValue(passphrasePasswordField.getText());
                sshProperties.sshUsernameProperty()
                        .setValue(userNamerTextField.getText());
                sshProperties.passPhraseSetProperty()
                        .setValue(true);
                postDialogAction.run();
                nodeUtility.closeNode(event);
            } else {
                alertUtility.warningAlert("Fields not Populated", "Username and passphrase cannot be blank when no passphrase is not selected.");
            }
        });
        noPassphraseCheckbox.selectedProperty().addListener((obj, oldVal, newVal) -> {
            passphrasePasswordField.disableProperty().setValue(newVal);
            userNamerTextField.requestFocus();
        });
    }

    private boolean validateTextField() {
        return (!passphrasePasswordField.getText()
                .isBlank() || noPassphraseCheckbox.isSelected()) && !userNamerTextField.getText()
                .isBlank();
    }
}
