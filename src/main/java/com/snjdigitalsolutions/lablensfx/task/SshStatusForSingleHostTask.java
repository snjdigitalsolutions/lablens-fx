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

public class SshStatusForSingleHostTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshStatusForSingleHostTask.class);
    private final Long resourceID;
    private final SshService sshService;
    private final ComputeResourceProperties computeResourceProperties;

    public SshStatusForSingleHostTask(Long resourceId, ComputeResourceProperties computeResourceProperties, SshService sshService) {
        this.resourceID = resourceId;
        this.computeResourceProperties = computeResourceProperties;
        this.sshService = sshService;
    }

    @Override
    protected Void call() throws Exception {
        ComputeResource resource = computeResourceProperties.getComputeResourcesMap().get(resourceID);
        LOGGER.debug("Verifying host status: {}", resource.getHostName());
        try {
            String response = sshService.executeCommand(resource.getIpAddress(), resource.getSshPort(),  "whoami");
            LOGGER.debug("ssh command response: {}", response);
            if (!response.isEmpty()) {
                Platform.runLater(() -> {
                    SshStatus currentStatus = resource.getHostPanelLarge().getStatusIndicator()
                            .getHostSshStatus();
                    resource.getHostPanelLarge()
                            .getStatusIndicator()
                            .hostSshStatusProperty()
                            .set(SshStatus.ONLINE);
                    computeResourceProperties.getComputeResourceOnlineStatusMap().put(resourceID,SshStatus.ONLINE);
                    if (!currentStatus.equals(SshStatus.ONLINE)){
                        int value = computeResourceProperties.getHostsOnline();
                        computeResourceProperties.hostsOnlineProperty().setValue(value + 1);
                    }
                });
            } else {
                decreaseOnlineCount(resource);
            }
        } catch (Exception e) {
            //TODO fix up logging for command errors
            LOGGER.error("Error executing command: {}", e.getMessage());
            decreaseOnlineCount(resource);
        }
        return null;
    }

    private void decreaseOnlineCount(ComputeResource resource) {
        Platform.runLater(() -> {
            SshStatus currentStatus = resource.getHostPanelLarge().getStatusIndicator()
                    .getHostSshStatus();
            resource.getHostPanelLarge()
                    .getStatusIndicator()
                    .hostSshStatusProperty()
                    .set(SshStatus.OFFLINE);
            computeResourceProperties.getComputeResourceOnlineStatusMap().put(resourceID,SshStatus.OFFLINE);
            if (currentStatus.equals(SshStatus.ONLINE)){
                int value = computeResourceProperties.getHostsOnline();
                computeResourceProperties.hostsOnlineProperty().setValue(value - 1);
            }
        });
    }
}
