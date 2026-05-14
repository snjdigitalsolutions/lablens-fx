package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.model.FileSystemObjectModel;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import javafx.concurrent.Task;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * When files are removed from a remote system the database
 * needs to be cleaned up so no orphan files exist in the
 * database.
 */
@Component
@Scope("prototype")
public class FileSystemObjectModelCleanupTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemObjectModelCleanupTask.class);
    private final HostManagementService hostManagementService;
    @Setter
    private List<FileSystemObjectModel> fileSystemObjectModelList;
    @Setter
    private Runnable successRunnable;

    /**
     * Creates the cleanup task.
     *
     * @param hostManagementService the service used to remove stale file-system object models
     */
    public FileSystemObjectModelCleanupTask(HostManagementService hostManagementService) {
        this.hostManagementService = hostManagementService;
    }

    /**
     * Removes file-system object models that correspond to paths no longer present on the remote host.
     *
     * @return {@code null} on completion
     * @throws Exception if any removal operation fails
     */
    @Override
    protected Void call() throws Exception {
        fileSystemObjectModelList.forEach(model -> {
            Optional<ComputeResource> optComputeResource = hostManagementService.getComputerResourceById(model.getComputeResourceID());
            optComputeResource.ifPresent(resource -> {
                boolean removed = resource.getFileSystemObjects()
                        .removeIf(fileSystemObject -> fileSystemObject.getParentPath()
                                .equalsIgnoreCase(model.getParentPath()));
                if (removed) {
                    LOGGER.debug("Removed file system object from resource");
                    hostManagementService.updateComputeResource(resource);
                }
            });
        });
        return null;
    }

    /**
     * Invokes the success runnable after the cleanup task completes.
     */
    @Override
    protected void succeeded() {
        super.succeeded();
        successRunnable.run();
    }

    /**
     * Invokes the success runnable even when the cleanup task fails.
     */
    @Override
    protected void failed() {
        super.failed();
        successRunnable.run();
    }
}
