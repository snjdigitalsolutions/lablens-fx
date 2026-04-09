package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class SshStatusForSingleHostTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshStatusForSingleHostTask.class);
    private final Long resourceID;
    private final SshService sshService;
    private final HostManagementService hostManagementService;

    public SshStatusForSingleHostTask(Long resourceId,
                                      ComputeResourceState computeResourceState,
                                      SshService sshService,
                                      HostManagementService hostManagementService
    )
    {
        this.resourceID = resourceId;
        this.sshService = sshService;
        this.hostManagementService = hostManagementService;
    }

    @Override
    protected Void call() throws Exception {
        Optional<ComputeResource> optionalResource = hostManagementService.getComputerResourceById(resourceID);
        if (optionalResource.isPresent()) {
            ComputeResource resource = optionalResource.get();
            LOGGER.debug("Verifying host status: {}", resource.getHostName());
            try {
                String response = sshService.executeCommand(resource.getIpAddress(), resource.getSshPort(), "whoami");
                LOGGER.debug("ssh command response: {}", response);
                if (!response.isEmpty()) {
                    Platform.runLater(() -> {
                        hostManagementService.setResourceStateOnline(resource);
                    });
                } else {
                    Platform.runLater(() -> {
                        hostManagementService.setResourceStateOffline(resource);
                    });
                }
            } catch (Exception e) {
                LOGGER.error("Error executing command: {}", e.getMessage());
                Platform.runLater(() -> {
                    hostManagementService.setResourceStateOffline(resource);
                });
            }
        }
        return null;
    }
}
