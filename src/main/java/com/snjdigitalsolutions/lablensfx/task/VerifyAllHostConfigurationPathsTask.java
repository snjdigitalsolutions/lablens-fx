package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.nodes.ProgressDialog;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.command.CheckElevatedPrivilegesRequiredCommand;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class VerifyAllHostConfigurationPathsTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyAllHostConfigurationPathsTask.class);

    private final ComputeResourceState computeResourceState;
    private final CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand;
    private final ProgressDialog progressDialog;
    private final Consumer<List<ComputeResource>> onSuccess;
    private final List<ComputeResource> changedComputeResources;

    /**
     * Creates the task that verifies configuration path accessibility across all selected hosts.
     *
     * @param computeResourceState                 the application state holding selected resources
     * @param checkElevatedPrivilegesRequiredCommand the command used to probe path elevation requirements
     * @param progressDialog                       the dialog that shows verification progress
     * @param onSuccess                            consumer called with changed resources when the task succeeds
     */
    public VerifyAllHostConfigurationPathsTask(ComputeResourceState computeResourceState,
                                               CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand,
                                               ProgressDialog progressDialog,
                                               Consumer<List<ComputeResource>> onSuccess) {
        this.computeResourceState = computeResourceState;
        this.checkElevatedPrivilegesRequiredCommand = checkElevatedPrivilegesRequiredCommand;
        this.progressDialog = progressDialog;
        this.onSuccess = onSuccess;
        this.changedComputeResources = new ArrayList<>();
    }

    /**
     * Iterates all selected hosts and their configuration paths, probing each for elevation requirements.
     *
     * @return {@code null} on completion
     * @throws Exception if any remote check operation fails
     */
    @Override
    protected Void call() throws Exception {
        try {
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
                                            path.requiresElevation().setValue(checkElevatedPrivilegesRequiredCommand.performCommand(resource, path.getConfigurationPath()));
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
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    /**
     * Invokes the success consumer with changed resources and closes the progress dialog.
     */
    @Override
    public void succeeded() {
        super.succeeded();
        onSuccess.accept(changedComputeResources);
        progressDialog.closeDialog();
    }

    /**
     * Closes the progress dialog when the task fails.
     */
    @Override
    public void failed() {
        super.failed();
        progressDialog.closeDialog();
    }

}
