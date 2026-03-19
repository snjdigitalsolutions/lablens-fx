package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.properties.SshProperties;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class PassphraseDialog extends GridPane implements SpringInitializableNode {

    @FXML
    private PasswordField passphrasePasswordField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button submitButton;

    private final NodeUtility nodeUtility;
    private final SshProperties sshProperties;
    @Setter
    private Runnable postDialogAction;

    public PassphraseDialog(@Value("classpath:/fxml/PassphraseDialog.fxml") Resource fxml,
                            NodeUtility nodeUtility,
                            SshProperties sshProperties) {
        this.nodeUtility = nodeUtility;
        this.sshProperties = sshProperties;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        cancelButton.setOnAction(nodeUtility::closeNode);
        submitButton.setOnAction(event -> {
            if (validateTextField() && postDialogAction != null) {
                sshProperties.passPhraseProperty()
                        .setValue(passphrasePasswordField.getText());
                sshProperties.passPhraseSetProperty()
                        .setValue(true);
                postDialogAction.run();
                nodeUtility.closeNode(event);
            }
        });
    }

    private boolean validateTextField() {
        return !passphrasePasswordField.getText()
                .isBlank();
    }
}
