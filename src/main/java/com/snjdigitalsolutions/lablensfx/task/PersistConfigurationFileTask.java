package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.FileStorage;
import com.snjdigitalsolutions.lablensfx.orm.FileSystemObject;
import com.snjdigitalsolutions.lablensfx.repository.FileStorageRepository;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import com.snjdigitalsolutions.lablensfx.utility.FilePathValidator;
import com.snjdigitalsolutions.lablensfx.utility.MD5Utility;
import javafx.concurrent.Task;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Scope("prototype")
public class PersistConfigurationFileTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistConfigurationFileTask.class);
    @Value("${application.scp.localstorage}")
    private String fileScpLocation;
    private final SshService sshService;
    private final FilePathValidator filePathValidator;
    private final MD5Utility md5Utility;
    private final HostManagementService hostManagementService;
    @Setter
    private Map<ComputeResource, List<FileSystemObject>> unpersistedFiles;

    /**
     * Creates the persistence task with required service and state dependencies.
     */
    public PersistConfigurationFileTask(SshService sshService,
                                        FilePathValidator filePathValidator,
                                        MD5Utility md5Utility,
                                        HostManagementService hostManagementService
    ) {
        this.sshService = sshService;
        this.filePathValidator = filePathValidator;
        this.md5Utility = md5Utility;
        this.hostManagementService = hostManagementService;
    }

    /**
     * Persists tracked configuration files that have not yet been saved to the database.
     *
     * @return {@code null} on completion
     * @throws Exception if any file transfer or hashing operation fails
     */
    @Override
    protected Void call() throws Exception {
        unpersistedFiles.keySet().forEach(hostKey -> {
            List<FileSystemObject> fileObjects = unpersistedFiles.get(hostKey);
            fileObjects.forEach(fileObject -> {
                String absolutePath = fileObject.getParentPath() + "/" + fileObject.getFileName();
                try {
                    //Create destination directory
                    Optional<String> destPath = filePathValidator.allValid(fileScpLocation, hostKey.getIpAddress(),fileObject.getParentPath());

                    if (destPath.isPresent()){
                        File destDir = new File(destPath.get());
                        if (!destDir.exists()){
                            destDir.mkdirs();
                        }
                        if (destDir.exists()){
                            String destFile = destPath.get() + "/" + fileObject.getFileName();

                            // Copy file to local system
                            sshService.secureCopyFileFromHost(hostKey.getIpAddress(), hostKey.getSshPort(), absolutePath, destFile);
                            LOGGER.debug("Successfully copied file to data directory");

                            //Get MD5 of file
                            String hash = md5Utility.calculate(destFile);

                            //Create file storage object
                            FileStorage fileStorageObject = new FileStorage();
                            fileStorageObject.setFileMd5(hash);
                            fileStorageObject.setAbsolutePath(absolutePath);
                            fileStorageObject.setFileSize(md5Utility.getFileSize(destFile));
                            fileStorageObject.setFileDate(md5Utility.getFileBytes(destFile));
                            fileStorageObject.setCreatedTime(Instant.ofEpochMilli(System.currentTimeMillis()));
                            fileStorageObject.setComputeResource(hostKey);
                            hostKey.getFileStorages().add(fileStorageObject);
                            hostManagementService.updateComputeResource(hostKey);
                        }
                    }



                } catch (Exception e) {
                    LOGGER.debug(e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        });
        return null;
    }
}
