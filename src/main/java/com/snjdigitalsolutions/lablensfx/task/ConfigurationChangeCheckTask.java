package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.repository.FileStorageRepository;
import com.snjdigitalsolutions.lablensfx.service.command.MD5SumCommand;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.utility.PermissionForPathUtility;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

    public ConfigurationChangeCheckTask(MD5SumCommand md5SumCommand,
                                        PermissionForPathUtility permissionForPathUtility,
                                        ComputeResourceState computeResourceState) {
        this.md5SumCommand = md5SumCommand;
        this.permissionForPathUtility = permissionForPathUtility;
        this.computeResourceState = computeResourceState;
    }

    @Override
    protected Void call() throws Exception {
        LOGGER.debug("Starting configuration change check...");
        AtomicInteger changeCount = new AtomicInteger(0);
        AtomicBoolean updateResource = new AtomicBoolean(false);
        Map<Long, ComputeResource> idResourceMap = computeResourceState.getComputeResourcesMap();
        idResourceMap.values().forEach(computeResource -> {
            computeResource.getFileStorages().forEach(fileStorage -> {
                boolean elevationNeeded = permissionForPathUtility.pathNeedsPermissionElevated(fileStorage.getAbsolutePath(), computeResource);
                LOGGER.debug("File being checked by hash: {} {} {}", computeResource.getIpAddress(), fileStorage.getAbsolutePath(), elevationNeeded);
                try {
                    String fileHash = md5SumCommand.performCommand(computeResource, fileStorage.getAbsolutePath(), elevationNeeded);
                    if (fileStorage.getFileMd5().contentEquals(fileHash)){
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
            if (updateResource.get()){
                computeResourceState.updateComputeResource(computeResource);
                updateResource.set(false);
            }
        });
        Platform.runLater(() -> {
            computeResourceState.configurationChangeCountProperty().setValue(changeCount.get());
        });
        LOGGER.debug("Finished configuration change check...");
        return null;
    }
}
