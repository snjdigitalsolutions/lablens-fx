package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.application.ChangeListenerRegistry;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.FileSystemObject;
import com.snjdigitalsolutions.lablensfx.repository.FileStorageRepository;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.task.PersistConfigurationFileTask;
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
public class FilePersistenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilePersistenceService.class);

    private final HostManagementService hostManagementService;
    private final FileStorageRepository fileStorageRepository;
    private final ObjectProvider<PersistConfigurationFileTask> persistConfigurationFileTaskProvider;
    private final ComputeResourceState computeResourceState;

    public FilePersistenceService(HostManagementService hostManagementService,
                                  FileStorageRepository fileStorageRepository,
                                  ObjectProvider<PersistConfigurationFileTask> persistConfigurationFileTaskProvider,
                                  ComputeResourceState computeResourceState,
                                  ChangeListenerRegistry changeListenerRegistry
    ) {
        this.hostManagementService = hostManagementService;
        this.fileStorageRepository = fileStorageRepository;
        this.persistConfigurationFileTaskProvider = persistConfigurationFileTaskProvider;
        this.computeResourceState = computeResourceState;
    }

    @Scheduled(fixedDelay = 60000)
    public void updateConfigurationFilePersistence(){
        if (computeResourceState.isComputeResourcesLoaded()){
            Map<ComputeResource, List<FileSystemObject>> mapOfUnpersistedFiles = findUnpersistedTrackedFiles();
            if (!mapOfUnpersistedFiles.isEmpty()) {
                PersistConfigurationFileTask task = persistConfigurationFileTaskProvider.getObject();
                task.setUnpersistedFiles(mapOfUnpersistedFiles);
                Thread.ofVirtual().start(task);
            }
        }
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

    public boolean isFilePersisted(ComputeResource computeResource, String absoluteFilePath) {
        return fileStorageRepository.existsByComputeResourceAndAbsolutePath(computeResource, absoluteFilePath);
    }
}
