package com.snjdigitalsolutions.lablensfx.service.node;

import com.snjdigitalsolutions.lablensfx.nodes.tableview.ConfigurationPathTableView;
import com.snjdigitalsolutions.lablensfx.nodes.tableview.PathFilesTableView;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.lablensfx.orm.FileSystemObject;
import com.snjdigitalsolutions.lablensfx.orm.model.FileSystemObjectModel;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.lablensfx.service.command.CheckElevatedPrivilegesRequiredCommand;
import com.snjdigitalsolutions.lablensfx.service.command.ListFileCommand;
import com.snjdigitalsolutions.lablensfx.service.command.commandparser.ListFileParser;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.task.FileSystemObjectModelCleanupTask;
import com.snjdigitalsolutions.lablensfx.task.ListFilesTask;
import com.snjdigitalsolutions.lablensfx.task.VerifySingleHostConfigurationPathTask;
import com.snjdigitalsolutions.lablensfx.utility.FilePathValidator;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Service
public class ConfigurationPaneService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationPaneService.class);

    private final ComputeResourceState computeResourceState;
    private final FilePathValidator filePathValidator;
    private final AlertUtility alertUtility;
    private final ConfigurationPathTableView configurationPathTableView;
    private final PathFilesTableView pathFilesTableView;
    private final CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand;
    private final ListFileCommand listFileCommand;
    private final ListFileParser listFileParser;
    private final StatusBarService statusBarService;
    private final ObjectProvider<ListFilesTask> listFilesTaskObjectProvider;
    private final ObjectProvider<FileSystemObjectModelCleanupTask> fileSystemObjectModelCleanupTaskObjectProvider;

    /**
     * Creates the configuration pane service with all required state and repository dependencies.
     */
    public ConfigurationPaneService(ComputeResourceState computeResourceState,
                                    ComputeResourceRepository computeResourceRepository,
                                    FilePathValidator filePathValidator,
                                    AlertUtility alertUtility,
                                    ConfigurationPathTableView configurationPathTableView, PathFilesTableView pathFilesTableView,
                                    CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand,
                                    ListFileCommand listFileCommand,
                                    ListFileParser listFileParser, StatusBarService statusBarService,
                                    ObjectProvider<ListFilesTask> listFilesTaskObjectProvider,
                                    ObjectProvider<FileSystemObjectModelCleanupTask> fileSystemObjectModelCleanupTaskObjectProvider
    ) {
        this.computeResourceState = computeResourceState;
        this.filePathValidator = filePathValidator;
        this.alertUtility = alertUtility;
        this.configurationPathTableView = configurationPathTableView;
        this.pathFilesTableView = pathFilesTableView;
        this.checkElevatedPrivilegesRequiredCommand = checkElevatedPrivilegesRequiredCommand;
        this.listFileCommand = listFileCommand;
        this.listFileParser = listFileParser;
        this.statusBarService = statusBarService;
        this.listFilesTaskObjectProvider = listFilesTaskObjectProvider;
        this.fileSystemObjectModelCleanupTaskObjectProvider = fileSystemObjectModelCleanupTaskObjectProvider;
    }

    /**
     * Removes the given configuration path from the currently selected compute resource.
     *
     * @param configurationPath the configuration path to remove
     */
    public void removeConfigurationPathFromSelectedResource(ConfigurationPath configurationPath) {
        if (computeResourceState.getSelectedResources()
                .size() == 1) {
            ComputeResource selectedResource = computeResourceState.getSelectedResources()
                    .getFirst();
            selectedResource.getConfigurationPaths()
                    .remove(configurationPath);
            computeResourceState.updateComputeResource(selectedResource);
        }
    }

    /**
     * Returns all configuration paths associated with the currently selected compute resource.
     *
     * @return list of configuration paths for the selected resource
     */
    public List<ConfigurationPath> getConfigurationPathsForSelectedResource() {
        List<ConfigurationPath> hostPaths = new ArrayList<>();
        if (computeResourceState.getSelectedResources()
                .size() == 1) {
            hostPaths.addAll(computeResourceState.getSelectedResources()
                    .getFirst()
                    .getConfigurationPaths());
        }
        return hostPaths;
    }

    /**
     * Adds a new configuration path to the currently selected compute resource and persists it.
     *
     * @param configurationPath the configuration path to add
     * @return {@code true} if the path was added; {@code false} if it was a duplicate or no resource is selected
     */
    public boolean addPathToSelectedResource(ConfigurationPath configurationPath) {
        boolean success = false;
        AtomicBoolean isDuplicate = new AtomicBoolean(false);
        if (computeResourceState.isSingleSourceSelected()) {
            Long resourceId = computeResourceState.getSelectedResources()
                    .getFirst()
                    .getId();
            ComputeResource resource = computeResourceState.getComputeResourcesMap()
                    .get(resourceId);
            List<ConfigurationPath> resourcePaths = resource.getConfigurationPaths();
            if (resourcePaths.isEmpty()) {
                resourcePaths.add(configurationPath);
            } else {
                resourcePaths.forEach(path -> {
                    if (path.getConfigurationPath()
                            .equals(configurationPath.getConfigurationPath())) {
                        isDuplicate.set(true);
                    }
                });
                if (!isDuplicate.get()) {
                    resourcePaths.add(configurationPath);
                }
            }

            if (!isDuplicate.get()) {
                configurationPath.setComputeResource(resource);
                computeResourceState.updateComputeResource(resource);
                success = true;
            }
        }
        return success;
    }

    /**
     * Validates the entered path, adds it to the selected resource, and triggers an elevation check.
     *
     * @param filePathTextField the text field containing the path entered by the user
     */
    public void addButtonAction(TextField filePathTextField) {
        if (filePathValidator.isValid(filePathTextField.getText())) {
            ConfigurationPath path = creteNewConfigurationPath(filePathTextField);
            if (!addPathToSelectedResource(path)) {
                alertUtility.warningAlert("Not Added", "Unable to add configuration path to host. Check for duplicate entry");
            } else {
                filePathTextField.clear();
                loadExistingPaths();

                //Start task for verifying privilege
                VerifySingleHostConfigurationPathTask singleHostConfigurationPathTask = new VerifySingleHostConfigurationPathTask(checkElevatedPrivilegesRequiredCommand, computeResourceState);
                Thread.ofVirtual().start(singleHostConfigurationPathTask);
            }
        } else {
            alertUtility.warningAlert("Invalid Path", "The path entered is not a valid system path.");
        }
    }

    /**
     * Constructs a new {@link ConfigurationPath} entity from the given text field value.
     *
     * @param filePathTextField the text field whose value becomes the path string
     * @return a new, un-persisted {@link ConfigurationPath} with elevation checks pending
     */
    @NonNull
    private ConfigurationPath creteNewConfigurationPath(TextField filePathTextField) {
        ConfigurationPath path = new ConfigurationPath();
        path.setConfigurationPath(filePathTextField.getText());
        path.setRequiresElevation(false);
        path.setElevationCheckComplete(false);
        return path;
    }

    /**
     * Reloads configuration paths and clears the files table for the currently selected resource.
     */
    public void loadExistingPaths() {
        configurationPathTableView.clearItems();
        pathFilesTableView.clearItems();
        getConfigurationPathsForSelectedResource().forEach(configurationPathTableView::addItem);
    }

    /**
     * Starts a background task to list all files under the given configuration path and populates the files table.
     *
     * @param configurationPath the path whose files should be listed
     */
    public void listFilesForConfigurationPath(ConfigurationPath configurationPath) {
        try {
            statusBarService.addLoadingFilesMessage();
            Consumer<List<FileSystemObjectModel>> fileSystemObjectConsumer = response -> {
                response.sort((o1,o2) -> o1.getFileName().compareToIgnoreCase(o2.getFileName()));
                pathFilesTableView.getItems().clear();
                pathFilesTableView.getItems().addAll(response);
                List<FileSystemObjectModel> nonExistantFileModels = response.stream().filter(FileSystemObjectModel::isNonExistantFile).toList();
                if (!nonExistantFileModels.isEmpty()){
                    alertUtility.confirmAlert("Files Not on File System", "One or more files have been identified as no longer on the file system. Do you want to remove them from the database?", () -> {
                        FileSystemObjectModelCleanupTask task = fileSystemObjectModelCleanupTaskObjectProvider.getObject();
                        task.setFileSystemObjectModelList(nonExistantFileModels);
                        task.setSuccessRunnable(this::loadExistingPaths);
                        Thread.ofVirtual().start(task);
                    });
                }
                statusBarService.removeLoadingFilesMessage();
            };
            ListFilesTask listTask = listFilesTaskObjectProvider.getObject();
            listTask.setConfigurationPath(configurationPath);
            listTask.setListConsumer(fileSystemObjectConsumer);
            Thread.ofVirtual().start(listTask);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
