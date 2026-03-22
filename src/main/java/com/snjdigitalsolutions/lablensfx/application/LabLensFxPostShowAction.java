package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.lablensfx.nodes.PassphraseDialog;
import com.snjdigitalsolutions.lablensfx.properties.SshProperties;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.StageNodeBuilder;
import com.snjdigitalsolutions.springbootutilityfx.splash.PostShowRunnable;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LabLensFxPostShowAction implements PostShowRunnable {

    @Value("${application.ssh.promptforpassphrase}")
    private Boolean promptForPassPhrase;
    private final HostManagementService hostManagementService;
    private final SshProperties sshProperties;
    private final PassphraseDialog passphraseDialog;
    private final Environment environment;

    public LabLensFxPostShowAction(HostManagementService hostManagementService,
                                   SshProperties sshProperties,
                                   PassphraseDialog passphraseDialog,
                                   Environment environment) {
        this.hostManagementService = hostManagementService;
        this.sshProperties = sshProperties;
        this.passphraseDialog = passphraseDialog;
        this.environment = environment;
    }

    @Override
    public void performPostSHowAction() {
        if (promptForPassPhrase) {
            StageNodeBuilder.builder()
                    .setNode(passphraseDialog)
                    .setTitle("SSH Passphrase")
                    .setModality(Modality.APPLICATION_MODAL)
                    .setResizable(false)
                    .buildAndShow();
            passphraseDialog.setPostDialogAction(hostManagementService::loadComputeResources);
        } else {
            sshProperties.passPhraseProperty().setValue(environment.getProperty("application.ssh.passphrase"));
            sshProperties.sshUsernameProperty().setValue(environment.getProperty("application.ssh.username"));
            sshProperties.passPhraseSetProperty().setValue(true);
            hostManagementService.loadComputeResources();
        }

    }

}
