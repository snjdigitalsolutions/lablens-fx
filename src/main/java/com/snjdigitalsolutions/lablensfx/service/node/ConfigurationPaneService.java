package com.snjdigitalsolutions.lablensfx.service.node;

import com.snjdigitalsolutions.lablensfx.nodes.ConfigurationPathTableView;
import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.nodes.PathFilesTableView;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.lablensfx.orm.model.FileSystemObjectModel;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.lablensfx.service.command.CheckElevatedPrivilegesRequiredCommand;
import com.snjdigitalsolutions.lablensfx.service.command.ListFileCommand;
import com.snjdigitalsolutions.lablensfx.service.command.commandparser.ListFileParser;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.task.ListFilesTask;
import com.snjdigitalsolutions.lablensfx.task.VerifySingleHostConfigurationPathTask;
import com.snjdigitalsolutions.lablensfx.utility.FilePathValidator;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.TaskStarter;
import javafx.scene.control.TextField;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ConfigurationPaneService {

    private final ComputeResourceState computeResourceState;
    private final ComputeResourceRepository computeResourceRepository;
    private final FilePathValidator filePathValidator;
    private final AlertUtility alertUtility;
    private final ConfigurationPathTableView configurationPathTableView;
    private final CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand;
    private final ListFileCommand listFileCommand;
    private final ListFileParser listFileParser;

    public ConfigurationPaneService(ComputeResourceState computeResourceState,
                                    ComputeResourceRepository computeResourceRepository,
                                    FilePathValidator filePathValidator,
                                    AlertUtility alertUtility,
                                    ConfigurationPathTableView configurationPathTableView,
                                    CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand,
                                    ListFileCommand listFileCommand,
                                    ListFileParser listFileParser
    )
    {
        this.computeResourceState = computeResourceState;
        this.computeResourceRepository = computeResourceRepository;
        this.filePathValidator = filePathValidator;
        this.alertUtility = alertUtility;
        this.configurationPathTableView = configurationPathTableView;
        this.checkElevatedPrivilegesRequiredCommand = checkElevatedPrivilegesRequiredCommand;
        this.listFileCommand = listFileCommand;
        this.listFileParser = listFileParser;
    }

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

    @NonNull
    private ConfigurationPath creteNewConfigurationPath(TextField filePathTextField) {
        ConfigurationPath path = new ConfigurationPath();
        path.setConfigurationPath(filePathTextField.getText());
        path.setRequiresElevation(false);
        path.setElevationCheckComplete(false);
        return path;
    }

    public void loadExistingPaths() {
        configurationPathTableView.clearItems();
        getConfigurationPathsForSelectedResource().forEach(configurationPathTableView::addItem);
    }

    public void listFilesForConfigurationPath(PathFilesTableView pathFilesTableView, ConfigurationPath configurationPath) {
        try {
            ListFilesTask listTask = new ListFilesTask(listFileCommand, listFileParser, computeResourceState, configurationPath, response -> {
                List<FileSystemObjectModel> models = new ArrayList<>();
                response.forEach(file -> {
                   models.add(new FileSystemObjectModel(file));
                   pathFilesTableView.getItems().clear();
                   pathFilesTableView.getItems().addAll(models);
               });
            });
            Thread.ofVirtual().start(listTask);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
