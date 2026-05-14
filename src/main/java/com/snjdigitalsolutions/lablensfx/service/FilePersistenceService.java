package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.application.ChangeListenerRegistry;
import com.snjdigitalsolutions.lablensfx.nodes.tableview.PathFilesTableView;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.FileSystemObject;
import com.snjdigitalsolutions.lablensfx.repository.FileStorageRepository;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.task.PersistConfigurationFileTask;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilePersistenceService  {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilePersistenceService.class);

    private final FileStorageRepository fileStorageRepository;
    private final ObjectProvider<PersistConfigurationFileTask> persistConfigurationFileTaskProvider;
    private final ComputeResourceState computeResourceState;

    @Setter
    private HostManagementService hostManagementService;

    /**
     * Creates the persistence service with required repository and state dependencies.
     */
    public FilePersistenceService(FileStorageRepository fileStorageRepository,
                                  ObjectProvider<PersistConfigurationFileTask> persistConfigurationFileTaskProvider,
                                  ComputeResourceState computeResourceState,
                                  ChangeListenerRegistry changeListenerRegistry
    ) {
        this.fileStorageRepository = fileStorageRepository;
        this.persistConfigurationFileTaskProvider = persistConfigurationFileTaskProvider;
        this.computeResourceState = computeResourceState;
    }

    /**
     * Call whenever configuration files selections are
     * changed to verify persistence status. Files are
     * added and removed from the database depending
     * on selection state.
     */
    public void updateConfigurationFilePersistence(){
        if (computeResourceState.isComputeResourcesLoaded()){
            Map<ComputeResource, List<FileSystemObject>> mapOfUnpersistedFiles = findUnpersistedTrackedFiles();
            if (!mapOfUnpersistedFiles.isEmpty()) {
                PersistConfigurationFileTask task = persistConfigurationFileTaskProvider.getObject();
                task.setUnpersistedFiles(mapOfUnpersistedFiles);
                Thread.ofVirtual().start(task);
            }
        }
        LOGGER.debug("Configuration files checked to confirm persistence");
    }

    /**
     * Returns tracked files that have not yet been persisted to file_storage,
     * grouped by their compute resource.
     */
    public Map<ComputeResource, List<FileSystemObject>> findUnpersistedTrackedFiles() {
        Map<ComputeResource, List<FileSystemObject>> result = new LinkedHashMap<>();
        hostManagementService.getAllComputeResources().forEach(resource -> {
            List<FileSystemObject> unpersisted = resource.getFileSystemObjects().stream()
                    .filter(FileSystemObject::isTrackFile)
                    .filter(fso -> {
                        String absolutePath = fso.getParentPath() + "/" + fso.getFileName();
                        return !isFilePersisted(resource, absolutePath);
                    })
                    .toList();
            if (!unpersisted.isEmpty()) {
                LOGGER.debug("{} unpersisted tracked file(s) for resource {}",
                             unpersisted.size(), resource.getHostName());
                result.put(resource, unpersisted);
            } else {
                LOGGER.debug("All tracked files have been persisted for host: {}", resource.getHostName());
            }
        });
        return result;
    }

    /**
     * Checks whether the given file has already been persisted to storage for the specified host.
     *
     * @param computeResource  the compute resource (host) to check against
     * @param absoluteFilePath the absolute path of the file on the host
     * @return {@code true} if a storage record exists for this file and host
     */
    public boolean isFilePersisted(ComputeResource computeResource, String absoluteFilePath) {
        return fileStorageRepository.existsByComputeResourceAndAbsolutePath(computeResource, absoluteFilePath);
    }


}
