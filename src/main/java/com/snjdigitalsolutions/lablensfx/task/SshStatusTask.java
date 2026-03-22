package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.nodes.HostStatusDialog;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.ComputeResourceProperties;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        List<ComputeResource> resources = computeResourceProperties.getComputeResourcesMap()
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
                        resource.getHostPanelLarge()
                                .getStatusIndicator()
                                .hostSshStatusProperty()
                                .set(SshStatus.ONLINE);
                        computeResourceProperties.getComputeResourceOnlineStatusMap()
                                .put(resource.getId(), SshStatus.ONLINE);
                        int value = computeResourceProperties.getHostsOnline();
                        computeResourceProperties.hostsOnlineProperty()
                                .setValue(value + 1);
                    });
                } else {
                    decreaseOnlineCount(resource);
                }
            } catch (Exception e) {
                LOGGER.error("Exception thrown when executing command", e);
                decreaseOnlineCount(resource);
            }
            LOGGER.debug("Updating {} of {}", resourceCheckIndex.get(), numberOfResources);
            updateProgress(resourceCheckIndex.get(), numberOfResources);
            resourceCheckIndex.incrementAndGet();
        });
        return null;
    }

    private void decreaseOnlineCount(ComputeResource resource) {
        Platform.runLater(() -> {
            resource.getHostPanelLarge()
                    .getStatusIndicator()
                    .hostSshStatusProperty()
                    .set(SshStatus.OFFLINE);
            computeResourceProperties.getComputeResourceOnlineStatusMap()
                    .put(resource.getId(), SshStatus.OFFLINE);
            int value = computeResourceProperties.getHostsOnline();
            if (value > 0) {
                computeResourceProperties.hostsOnlineProperty()
                        .setValue(value - 1);
            }
        });
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

    @Override
    protected void failed() {
        super.failed();
        LOGGER.error("SSH status Task failed...");
        hostStatusDialog.closeDialog();
    }
}
