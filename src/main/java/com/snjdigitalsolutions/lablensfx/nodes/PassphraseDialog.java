package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.state.SshState;
import com.snjdigitalsolutions.lablensfx.service.PassPhraseMode;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.StageNodeBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    private final SshState sshState;
    private final AlertUtility alertUtility;
    @Setter
    private Runnable postDialogAction;

    public PassphraseDialog(@Value("classpath:/fxml/PassphraseDialog.fxml") Resource fxml,
                            NodeUtility nodeUtility,
                            SshState sshState,
                            AlertUtility alertUtility) {
        this.nodeUtility = nodeUtility;
        this.sshState = sshState;
        this.alertUtility = alertUtility;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        Runnable submitAction = () -> {
            if (validateTextField() && postDialogAction != null) {
                sshState.passPhraseProperty()
                        .setValue(passphrasePasswordField.getText());
                sshState.sshUsernameProperty()
                        .setValue(userNamerTextField.getText());
                sshState.passPhraseModeProperty().setValue(PassPhraseMode.PROVIDED);
                postDialogAction.run();
                if (this.getScene() != null && this.getScene().getWindow() != null){
                    ((Stage)this.getScene().getWindow()).close();
                }
            } else if (!noPassphraseCheckbox.isSelected()) {
                alertUtility.warningAlert("Fields not Populated", "Username and passphrase can not be blank.");
            } else if (noPassphraseCheckbox.isSelected()){
                alertUtility.warningAlert("Fields not populated", "Username field can not be blank.");
            }
        };
        cancelButton.setOnAction(nodeUtility::closeNode);
        submitButton.setOnAction(event -> {
            submitAction.run();
        });
        noPassphraseCheckbox.selectedProperty().addListener((obj, oldVal, newVal) -> {
            passphrasePasswordField.disableProperty().setValue(newVal);
            if (newVal){
                sshState.passPhraseModeProperty().setValue(PassPhraseMode.NOT_NEEDED);
            }
            userNamerTextField.requestFocus();
        });
        passphrasePasswordField.setOnKeyPressed(value ->{
            if (value.getCode().equals(KeyCode.ENTER)){
                submitAction.run();
            }
        });
    }

    private boolean validateTextField() {
        return (!passphrasePasswordField.getText()
                .isBlank() || noPassphraseCheckbox.isSelected()) && !userNamerTextField.getText()
                .isBlank();
    }

    public void showDialog() {
        StageNodeBuilder.builder()
                .setNode(this)
                .setTitle("SSH Passphrase")
                .setModality(Modality.APPLICATION_MODAL)
                .setResizable(false)
                .buildAndShow();
        userNamerTextField.requestFocus();
    }
}
