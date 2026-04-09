package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.nodes.ProgressDialog;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SshStatusTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshStatusTask.class);
    private final ComputeResourceState computeResourceState;
    private final ProgressDialog progressDialog;
    private final SshService sshService;
    private final HostManagementService hostManagementService;

    public SshStatusTask(ComputeResourceState computeResourceState,
                         ProgressDialog progressDialog,
                         SshService sshService,
                         HostManagementService hostManagementService
    )
    {
        this.computeResourceState = computeResourceState;
        this.progressDialog = progressDialog;
        this.sshService = sshService;
        this.hostManagementService = hostManagementService;
    }

    @Override
    protected Void call() throws Exception {
        List<ComputeResource> resources = computeResourceState.getComputeResourcesMap()
                .values()
                .stream()
                .filter(resource -> resource.getSshCommunicate() > 0)
                .toList();
        int numberOfResources = resources.size();
        AtomicInteger resourceCheckIndex = new AtomicInteger(1);
        resources.forEach(resource -> {
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
                LOGGER.error("Exception thrown when executing command", e);
                Platform.runLater(() -> {
                    hostManagementService.setResourceStateOffline(resource);
                });
            }
            LOGGER.debug("Updating {} of {}", resourceCheckIndex.get(), numberOfResources);
            updateProgress(resourceCheckIndex.get(), numberOfResources);
            resourceCheckIndex.incrementAndGet();
        });
        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        progressDialog.closeDialog();
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        progressDialog.closeDialog();
    }

    @Override
    protected void failed() {
        super.failed();
        LOGGER.error("SSH status Task failed...");
        progressDialog.closeDialog();
    }
}
