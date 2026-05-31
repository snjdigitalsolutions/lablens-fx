package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.command.MD5SumCommand;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.state.ConfigurationCheckState;
import com.snjdigitalsolutions.lablensfx.utility.PermissionForPathUtility;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Scope("prototype")
public class ConfigurationChangeCheckTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationChangeCheckTask.class);

    private final MD5SumCommand md5SumCommand;
    private final PermissionForPathUtility permissionForPathUtility;
    private final ComputeResourceState computeResourceState;
    private final ConfigurationCheckState configurationCheckState;

    /**
     * Creates the configuration-change check task with required state and service dependencies.
     */
    public ConfigurationChangeCheckTask(MD5SumCommand md5SumCommand,
                                        PermissionForPathUtility permissionForPathUtility,
                                        ComputeResourceState computeResourceState,
                                        ConfigurationCheckState configurationCheckState
    )
    {
        this.md5SumCommand = md5SumCommand;
        this.permissionForPathUtility = permissionForPathUtility;
        this.computeResourceState = computeResourceState;
        this.configurationCheckState = configurationCheckState;
    }

    @Override
    public void run() {
        LOGGER.debug("Starting configuration change check...");
        Platform.runLater(() -> {
            configurationCheckState.checkStatusProperty()
                    .setValue("Checking Configurations...");
        });
        AtomicInteger changeCount = new AtomicInteger(0);
        AtomicBoolean updateResource = new AtomicBoolean(false);
        Map<Long, ComputeResource> idResourceMap = computeResourceState.getComputeResourcesMap();
        idResourceMap.values()
                .forEach(computeResource -> {
                    computeResource.getFileStorages()
                            .forEach(fileStorage -> {
                                boolean elevationNeeded = permissionForPathUtility.pathNeedsPermissionElevated(fileStorage.getAbsolutePath(), computeResource);
                                LOGGER.debug("File being checked by hash: {} {} {}", computeResource.getIpAddress(), fileStorage.getAbsolutePath(), elevationNeeded);
                                try {
                                    String fileHash = md5SumCommand.performCommand(computeResource, fileStorage.getAbsolutePath(), elevationNeeded);
                                    if (fileStorage.getFileMd5()
                                            .contentEquals(fileHash)) {
                                        LOGGER.debug("File content matches");
                                    } else {
                                        LOGGER.debug("File content does not match");
                                        fileStorage.setChangedOnDisk(true);
                                        updateResource.set(true);
                                        changeCount.incrementAndGet();
                                    }
                                } catch (Exception e) {
                                    LOGGER.error("Unable to verify file hash");
                                }
                            });
                    if (updateResource.get()) {
                        computeResourceState.updateComputeResource(computeResource);
                        updateResource.set(false);
                    }
                });
        Platform.runLater(() -> {
            computeResourceState.configurationChangeCountProperty()
                    .setValue(changeCount.get());
        });
        LOGGER.debug("Finished configuration change check...");
        Platform.runLater(() -> {
            LocalTime now = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String timeString = "Last Check: " + formatter.format(now);
            configurationCheckState.checkStatusProperty()
                    .setValue(timeString);
        });
    }

    /**
     * Checks whether any tracked configuration files have changed since the last scan.
     *
     * @return {@code null} on completion
     * @throws Exception if any SSH or hashing operation fails
     */
    @Override
    protected Void call() throws Exception {
        this.run();
        return null;
    }
}
