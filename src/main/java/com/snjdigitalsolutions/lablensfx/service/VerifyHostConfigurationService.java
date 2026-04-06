package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.nodes.ProgressDialog;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.lablensfx.service.command.CheckElevatedPrivilegesRequiredCommand;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.task.VerifyHostConfigurationPathTask;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.StageNodeBuilder;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.TaskStarter;
import javafx.concurrent.Task;
import javafx.stage.Modality;
import org.springframework.stereotype.Service;

@Service
public class VerifyHostConfigurationService implements TaskStartingService {

    private final ComputeResourceState computeResourceState;
    private final CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand;
    private final ProgressDialog progressDialog;
    private final ComputeResourceRepository computeResourceRepository;

    public VerifyHostConfigurationService(ComputeResourceState computeResourceState,
                                          CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand,
                                          ProgressDialog progressDialog,
                                          ComputeResourceRepository computeResourceRepository) {
        this.computeResourceState = computeResourceState;
        this.checkElevatedPrivilegesRequiredCommand = checkElevatedPrivilegesRequiredCommand;
        this.progressDialog = progressDialog;
        this.computeResourceRepository = computeResourceRepository;
    }


    @Override
    public void startTask() {
        progressDialog.setProgressText("Verifying Unchecked Configuration Paths");
        StageNodeBuilder.builder()
                .setModality(Modality.APPLICATION_MODAL)
                .setResizable(false)
                .setTitle("Unchecked Paths")
                .setNode(progressDialog)
                .buildAndShow();
        Task<Void> task = new VerifyHostConfigurationPathTask(computeResourceState,
                checkElevatedPrivilegesRequiredCommand,
                progressDialog, computeResourceRepository::saveAll
        );
        progressDialog.getProgressBar()
                .progressProperty()
                .bind(task.progressProperty());
        progressDialog.setOnDialogClosed(() -> {
            if (task.isRunning()) {
                task.cancel();
            }
        });
        TaskStarter.startTask(task);
    }
}
