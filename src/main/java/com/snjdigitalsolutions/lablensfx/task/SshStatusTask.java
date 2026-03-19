package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.nodes.HostStatusDialog;
import com.snjdigitalsolutions.lablensfx.properties.ComputeResourceProperties;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class SshStatusTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshStatusTask.class);
    private final ComputeResourceProperties computeResourceProperties;
    private final HostStatusDialog hostStatusDialog;
    private final SshService sshService;

    public SshStatusTask(ComputeResourceProperties computeResourceProperties, HostStatusDialog hostStatusDialog, SshService sshService) {
        this.computeResourceProperties = computeResourceProperties;
        this.hostStatusDialog = hostStatusDialog;
        this.sshService = sshService;
    }

    @Override
    protected Void call() throws Exception {
        int numberOfResource = computeResourceProperties.getComputeResourcesMap()
                .size();
        AtomicInteger resourceCheckIndex = new AtomicInteger(1);
        computeResourceProperties.getComputeResourcesMap()
                .values()
                .forEach(resource -> {
                    if (resource.getSshCommunicate() > 0) {
                        LOGGER.debug("Verifying host status: {}", resource.getHostName());
                        try {
                            System.out.println(sshService.executeCommand(resource.getIpAddress(), resource.getSshPort(), "jparham", "whoami"));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            throw new RuntimeException(e);
                        }
                        updateProgress(resourceCheckIndex.get(), numberOfResource);
                        resourceCheckIndex.set(resourceCheckIndex.get() + 1);
                    }
                });
        return null;
    }

    @Override
    protected void updateProgress(long workDone, long max) {
        super.updateProgress(workDone, max);
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        hostStatusDialog.closeDialog();
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        hostStatusDialog.closeDialog();
    }
}
