package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.nodes.HostStatusDialog;
import com.snjdigitalsolutions.lablensfx.properties.ComputeResourceProperties;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
import javafx.application.Platform;
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
        AtomicInteger numberOfResources = new AtomicInteger(0);
        computeResourceProperties.getComputeResourcesMap().values().forEach(resource -> {
            if (resource.getSshCommunicate() > 0){
                numberOfResources.incrementAndGet();
            }
        });
        AtomicInteger resourceCheckIndex = new AtomicInteger(1);
        computeResourceProperties.getComputeResourcesMap()
                .values()
                .forEach(resource -> {
                    if (resource.getSshCommunicate() > 0) {
                        LOGGER.debug("Verifying host status: {}", resource.getHostName());
                        try {
                            String response = sshService.executeCommand(resource.getIpAddress(), resource.getSshPort(), "jparham", "whoami");
                            LOGGER.debug("ssh command response: {}", response);
                            if (!response.isEmpty()) {
                                Platform.runLater(() -> {
                                    resource.getHostPanelLarge()
                                            .getStatusIndicator()
                                            .hostSshStatusProperty()
                                            .set(SshStatus.ONLINE);
                                    computeResourceProperties.getComputeResourceOnlineStatusMap().put(resource.getId(), SshStatus.ONLINE);
                                    int value = computeResourceProperties.getHostsOnline();
                                    computeResourceProperties.hostsOnlineProperty().setValue(value + 1);
                                });
                            } else {
                                Platform.runLater(() -> {
                                    resource.getHostPanelLarge()
                                            .getStatusIndicator()
                                            .hostSshStatusProperty()
                                            .set(SshStatus.OFFLINE);
                                    computeResourceProperties.getComputeResourceOnlineStatusMap().put(resource.getId(), SshStatus.OFFLINE);
                                    int value = computeResourceProperties.getHostsOnline();
                                    computeResourceProperties.hostsOnlineProperty().setValue(value - 1);
                                });
                            }
                        } catch (Exception e) {
                            System.out.println("Error executing command: \n" + e.getMessage());
                            Platform.runLater(() -> {
                                resource.getHostPanelLarge()
                                        .getStatusIndicator()
                                        .hostSshStatusProperty()
                                        .set(SshStatus.OFFLINE);
                                computeResourceProperties.getComputeResourceOnlineStatusMap().put(resource.getId(), SshStatus.OFFLINE);
                                int value = computeResourceProperties.getHostsOnline();
                                computeResourceProperties.hostsOnlineProperty().setValue(value - 1);
                            });
                        }
                        LOGGER.debug("Updating {} of {}", resourceCheckIndex.get(), numberOfResources.get());
                        updateProgress(resourceCheckIndex.get(), numberOfResources.get());
                        resourceCheckIndex.set(resourceCheckIndex.get() + 1);
                    }
                });
        return null;
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
