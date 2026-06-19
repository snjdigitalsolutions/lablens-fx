package com.snjdigitalsolutions.lablensfx.graph;

import com.snjdigitalsolutions.lablensfx.orm.FileStorage;
import com.snjdigitalsolutions.lablensfx.repository.FileStorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ChangedConfigurationGraphBaseCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangedConfigurationGraphBaseCreator.class);
    private final FileStorageRepository fileStorageRepository;
    private final ObjectProvider<ChangedConfigurationGraphBase<FileStorage>> changedConfigurationGraphBaseProvider;

    public ChangedConfigurationGraphBaseCreator(FileStorageRepository fileStorageRepository,
                                                ObjectProvider<ChangedConfigurationGraphBase<FileStorage>> changedConfigurationGraphBaseProvider
    )
    {
        this.fileStorageRepository = fileStorageRepository;
        this.changedConfigurationGraphBaseProvider = changedConfigurationGraphBaseProvider;
    }

    /**
     * Collects all configuration paths which have unresolved changes.
     *
     * @return list of unresolved {@link ChangedConfigurationGraphBase}
     */
    public List<ChangedConfigurationGraphBase<FileStorage>> createChangedConfigurationGraphBase() {
        List<ChangedConfigurationGraphBase<FileStorage>> changedConfigurationGraphBaseList = new ArrayList<>();
        List<FileStorage> changedAndUnresolvedFiles = getChangedUnresolvedFileStorageAndChildren();
        Map<Integer, FileStorage> idToFileStorageMap = new HashMap<>();
        Map<Integer, FileStorage> childIdToFileStorageMap = new HashMap<>();
        changedAndUnresolvedFiles.forEach(fileStorage -> {
            idToFileStorageMap.put(fileStorage.getId()
                                           .intValue(), fileStorage);
        });
        idToFileStorageMap.values()
                .forEach(fileStorage -> {
                    if (!idToFileStorageMap.containsKey(fileStorage.getParent())) {
                        ChangedConfigurationGraphBase<FileStorage> changedConfigurationGraphBase = changedConfigurationGraphBaseProvider.getIfAvailable();
                        changedConfigurationGraphBase.getUnresolvedChangedFileStorages()
                                .add(fileStorage);
                        if (fileStorage.getChild() != 0) {
                            getFileStorageChild(changedConfigurationGraphBase, idToFileStorageMap, fileStorage);
                            LOGGER.debug("Adding to child list");
                            changedConfigurationGraphBaseList.add(changedConfigurationGraphBase);
                        }
                    }
                });
        return changedConfigurationGraphBaseList;
    }

    private List<FileStorage> getChangedUnresolvedFileStorageAndChildren() {
        List<FileStorage> childrenToAdd = new ArrayList<>();
        List<FileStorage> changedAndUnresolvedFiles = fileStorageRepository.findAllByChangedOnDiskAndResolved(true, false);
        changedAndUnresolvedFiles.forEach(fileStorage -> {
            if (fileStorage.getChild() != 0) {
                Optional<FileStorage> missingChild = fileStorageRepository.findById(fileStorage.getChild()
                                                                                            .longValue());
                missingChild.ifPresent(childrenToAdd::add);
            }
        });
        changedAndUnresolvedFiles.addAll(childrenToAdd);
        return changedAndUnresolvedFiles;
    }

    private void getFileStorageChild(ChangedConfigurationGraphBase<FileStorage> changedConfigurationGraphBase,
                                     Map<Integer, FileStorage> idToFileStorageMap,
                                     FileStorage fileStorage
    )
    {
        LOGGER.debug("FileStorage ID: {}", fileStorage.getId());
        if (fileStorage.getChild() != 0) {
            changedConfigurationGraphBase.getUnresolvedChangedFileStorages()
                    .add(idToFileStorageMap.get(fileStorage.getChild()));
            LOGGER.debug("Added file storage child id {}", fileStorage.getChild());
            if (idToFileStorageMap.containsKey(fileStorage.getChild())) {
                getFileStorageChild(changedConfigurationGraphBase, idToFileStorageMap, idToFileStorageMap.get(fileStorage.getChild()));
            }
        }
    }
}
