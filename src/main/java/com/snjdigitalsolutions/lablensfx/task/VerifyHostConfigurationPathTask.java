package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.nodes.ProgressDialog;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.command.CheckElevatedPrivilegesRequiredCommand;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class VerifyHostConfigurationPathTask extends Task<Void> {

    private final ComputeResourceState computeResourceState;
    private final CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand;
    private final ProgressDialog progressDialog;
    private final Consumer<List<ComputeResource>> onSuccess;
    private final List<ComputeResource> changedComputeResources;

    public VerifyHostConfigurationPathTask(ComputeResourceState computeResourceState,
                                           CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand,
                                           ProgressDialog progressDialog,
                                           Consumer<List<ComputeResource>> onSuccess) {
        this.computeResourceState = computeResourceState;
        this.checkElevatedPrivilegesRequiredCommand = checkElevatedPrivilegesRequiredCommand;
        this.progressDialog = progressDialog;
        this.onSuccess = onSuccess;
        this.changedComputeResources = new ArrayList<>();
    }

    @Override
    protected Void call() throws Exception {
        long numberOfUncheckedPaths = computeResourceState.getSelectedResources()
                .getFirst()
                .getConfigurationPaths()
                .stream()
                .filter(p -> p.getElevationCheckComplete() == false)
                .count();
        AtomicInteger checkIndex = new AtomicInteger(1);
        computeResourceState.getSelectedResources()
                .forEach(resource -> {
                    AtomicBoolean changed = new AtomicBoolean(false);
                    resource.getConfigurationPaths()
                            .forEach(path -> {
                                if (!path.getElevationCheckComplete()) {
                                    //Perform check and update
                                    try {
                                        path.requiresElevation().setValue(checkElevatedPrivilegesRequiredCommand.checkFilePath(resource, path.getConfigurationPath()));
                                        path.setElevationCheckComplete(true);
                                        changed.set(true);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                updateProgress(checkIndex.getAndIncrement(), numberOfUncheckedPaths);
                            });
                    if (changed.get()){
                        changedComputeResources.add(resource);
                    }
                });
        return null;
    }

    @Override
    public void succeeded() {
        super.succeeded();
        onSuccess.accept(changedComputeResources);
        progressDialog.closeDialog();
    }

    @Override
    public void failed() {
        super.failed();
        progressDialog.closeDialog();
    }

}
