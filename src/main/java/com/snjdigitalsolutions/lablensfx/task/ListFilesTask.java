package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.lablensfx.orm.FileSystemObject;
import com.snjdigitalsolutions.lablensfx.orm.model.FileSystemObjectModel;
import com.snjdigitalsolutions.lablensfx.repository.FileSystemObjectRepository;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.service.command.ListFileCommand;
import com.snjdigitalsolutions.lablensfx.service.command.commandparser.ListFileParser;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import javafx.concurrent.Task;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public class ListFilesTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListFilesTask.class);

    private final ListFileCommand listFileCommand;
    private final ListFileParser listFileParser;
    private final ComputeResourceState computeResourceState;
    private final HostManagementService hostManagementService;
    @Setter
    private ConfigurationPath configurationPath;
    @Setter
    private Consumer<List<FileSystemObjectModel>> listConsumer;
    private List<FileSystemObjectModel> hostFileList;
    private final Map<String, FileSystemObjectModel> fileNameToModelMap = new HashMap<>();

    public ListFilesTask(ListFileCommand listFileCommand,
                         ListFileParser listFileParser,
                         ComputeResourceState computeResourceState,
                         HostManagementService hostManagementService,
                         FileSystemObjectRepository fileSystemObjectRepository
    )
    {
        this.listFileCommand = listFileCommand;
        this.listFileParser = listFileParser;
        this.computeResourceState = computeResourceState;
        this.hostManagementService = hostManagementService;
    }

    @Override
    protected Void call() throws Exception {
        //Get list of filenames at selected configuration path
        List<String> listCommandResponse = listFileCommand.listFiles(computeResourceState.getSelectedResources()
                                                                             .getFirst(), configurationPath.getConfigurationPath(), configurationPath.getRequiresElevation());

        //Populate hostFileList with models generated from listing files on the remote host
        if (listCommandResponse != null && !listCommandResponse.isEmpty()) {
            hostFileList = listFileParser.getFileSystemObjectModels(configurationPath.getConfigurationPath(), listCommandResponse);
        }

        //Map models by filename
        hostFileList.forEach(model -> {
            fileNameToModelMap.put(model.getFileName(), model);
        });

        //Get objects from compute resource and replace model in map when exists in database
        List<FileSystemObject> databasedFiles = replaceFileSystemObjectsWithThoseFoundInDatabase();

        updateHostFileListWithMergedFiles();

        AtomicBoolean updateResource = addFileSystemObjectsToComputeResource(databasedFiles);
        if (updateResource.get()) {
            hostManagementService.updateComputeResource(configurationPath.getComputeResource());
        }

        return null;
    }

    private void updateHostFileListWithMergedFiles() {
        hostFileList.clear();
        hostFileList.addAll(fileNameToModelMap.values());
    }

    /**
     * When a model does not originate from the database, it was created
     * from the file system query and has not been persisted to the database.
     * This method creates the proper FileSystemObject and adds it to the
     * ComputeResource so that is can be persisted.
     *
     * @param databasedFiles list of files from the database contained in ComputeResource
     * @return true when FileSystemObject is created
     */
    @NonNull
    private AtomicBoolean addFileSystemObjectsToComputeResource(List<FileSystemObject> databasedFiles) {
        AtomicBoolean updateResource = new AtomicBoolean(false);
        hostFileList.forEach(model -> {
            if (model.isDbIsSource()) {
                LOGGER.debug("File from database: {}", model.getFileName());
            } else {
                LOGGER.debug("File not in database: {}", model.getFileName());
                FileSystemObject fileSystemObject = model.toFileSystemObject();
                fileSystemObject.setComputeResource(configurationPath.getComputeResource());
                databasedFiles.add(fileSystemObject);
                updateResource.set(true);
            }
        });
        return updateResource;
    }

    @NonNull
    private List<FileSystemObject> replaceFileSystemObjectsWithThoseFoundInDatabase() {
        List<FileSystemObject> databasedFiles = configurationPath.getComputeResource()
                .getFileSystemObjects();
        databasedFiles.forEach(file -> {
            if (fileNameToModelMap.containsKey(file.getFileName()) && file.getParentPath()
                    .equalsIgnoreCase(configurationPath.getConfigurationPath())) {
                fileNameToModelMap.put(file.getFileName(), new FileSystemObjectModel(file));
                LOGGER.debug("Model replaced in map for path: {}", file.getFileName());
            } else if (file.getParentPath()
                    .equalsIgnoreCase(configurationPath.getConfigurationPath())) {
                fileNameToModelMap.put(file.getFileName(), new FileSystemObjectModel(file, true));
                LOGGER.debug("Persistent file no longer on filesystem: {}", file.getFileName());
            }
        });
        return databasedFiles;
    }

    @Override
    public void succeeded() {
        listConsumer.accept(hostFileList);
    }

    @Override
    public void cancelled() {

    }


}
