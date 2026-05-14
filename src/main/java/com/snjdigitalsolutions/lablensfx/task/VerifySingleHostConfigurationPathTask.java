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

    /**
     * Creates the task that verifies configuration paths for a single host.
     *
     * @param checkElevatedPrivilegesRequiredCommand the command used to probe elevation requirements
     * @param computeResourceState                  the application state holding the selected resource
     */
    public VerifySingleHostConfigurationPathTask(CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand,
                                                 ComputeResourceState computeResourceState
    )
    {
        this.checkElevatedPrivilegesRequiredCommand = checkElevatedPrivilegesRequiredCommand;
        this.computeResourceState = computeResourceState;
    }

    /**
     * Probes each configuration path on the selected host to determine if elevation is required.
     *
     * @return {@code null} on completion
     * @throws Exception if any remote check operation fails
     */
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
                                        .setValue(checkElevatedPrivilegesRequiredCommand.performCommand(computeResource, path.getConfigurationPath()));
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
