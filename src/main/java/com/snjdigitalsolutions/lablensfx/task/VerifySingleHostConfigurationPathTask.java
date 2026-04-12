package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.command.CheckElevatedPrivilegesRequiredCommand;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class VerifySingleHostConfigurationPathTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifySingleHostConfigurationPathTask.class);


    private final CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand;
    private final ComputeResourceState computeResourceState;

    public VerifySingleHostConfigurationPathTask(CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand,
                                                 ComputeResourceState computeResourceState
    )
    {
        this.checkElevatedPrivilegesRequiredCommand = checkElevatedPrivilegesRequiredCommand;
        this.computeResourceState = computeResourceState;
    }

    @Override
    protected Void call() throws Exception {
        AtomicBoolean changed = new AtomicBoolean(false);
        if (computeResourceState.isSingleSourceSelected()) {
            ComputeResource computeResource = computeResourceState.getSelectedResources()
                    .getFirst();
            computeResource.getConfigurationPaths()
                    .forEach(path -> {
                        if (!path.getElevationCheckComplete()) {
                            //Perform check and update
                            try {
                                path.requiresElevation()
                                        .setValue(checkElevatedPrivilegesRequiredCommand.checkFilePath(computeResource, path.getConfigurationPath()));
                                path.setElevationCheckComplete(true);
                                changed.set(true);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
            if (changed.get()) {
                Platform.runLater(() -> {
                    computeResourceState.updateComputeResource(computeResource);
                });
            }
        }
        return null;
    }
}
