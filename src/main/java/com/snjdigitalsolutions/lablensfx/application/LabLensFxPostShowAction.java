package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.lablensfx.nodes.PassphraseDialog;
import com.snjdigitalsolutions.lablensfx.state.SshState;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.service.PassPhraseMode;
import com.snjdigitalsolutions.springbootutilityfx.splash.PostShowRunnable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LabLensFxPostShowAction implements PostShowRunnable {

    @Value("${application.ssh.promptforpassphrase}")
    private Boolean promptForPassPhrase;
    private final HostManagementService hostManagementService;
    private final SshState sshState;
    private final PassphraseDialog passphraseDialog;
    private final Environment environment;

    public LabLensFxPostShowAction(HostManagementService hostManagementService, SshState sshState, PassphraseDialog passphraseDialog, Environment environment) {
        this.hostManagementService = hostManagementService;
        this.sshState = sshState;
        this.passphraseDialog = passphraseDialog;
        this.environment = environment;
    }

    @Override
    public void performPostSHowAction() {
        if (promptForPassPhrase) {
            if (hostManagementService.sshNeededOnStartup()) {
                passphraseDialog.showDialog();
                passphraseDialog.setPostDialogAction(hostManagementService::loadComputeResources);
            }
        } else {
            sshState.passPhraseProperty()
                    .setValue(environment.getProperty("application.ssh.passphrase"));
            sshState.sshUsernameProperty()
                    .setValue(environment.getProperty("application.ssh.username"));
            sshState.passPhraseModeProperty()
                    .setValue(PassPhraseMode.PROVIDED);
            hostManagementService.loadComputeResources();
        }
    }

}
