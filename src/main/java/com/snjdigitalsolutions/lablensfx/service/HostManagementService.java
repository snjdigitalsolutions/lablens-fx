package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.nodes.HostPanelLarge;
import com.snjdigitalsolutions.lablensfx.nodes.ProgressDialog;
import com.snjdigitalsolutions.lablensfx.nodes.tableview.PathFilesTableView;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.lablensfx.orm.Setting;
import com.snjdigitalsolutions.lablensfx.orm.model.ComputeResourceModel;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.lablensfx.repository.SettingRepository;
import com.snjdigitalsolutions.lablensfx.service.node.HostPanelService;
import com.snjdigitalsolutions.lablensfx.setting.SettingType;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.state.SettingState;
import com.snjdigitalsolutions.lablensfx.state.SshState;
import com.snjdigitalsolutions.lablensfx.state.StatusBarState;
import com.snjdigitalsolutions.lablensfx.task.ConfigurationChangeCheckTask;
import com.snjdigitalsolutions.lablensfx.task.SshStatusForSingleHostTask;
import com.snjdigitalsolutions.lablensfx.task.SshStatusTask;
import com.snjdigitalsolutions.lablensfx.utility.DebugUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.StageNodeBuilder;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HostManagementService implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostManagementService.class);
    private final ComputeResourceState computeResourceState;
    private final StatusBarState statusBarProperties;
    private final ComputeResourceRepository computeResourceRepository;
    private final ObjectProvider<HostPanel> hostPanelProvider;
    private final ProgressDialog progressDialog;
    private final SshService sshService;
    private final SshState sshState;
    private final AlertUtility alertUtility;
    private final StatusBarState statusBarState;
    private final HostPanelService hostPanelService;
    private final PathFilesTableView pathFilesTableView;
    private final FilePersistenceService filePersistenceService;
    private final ObjectProvider<ConfigurationChangeCheckTask> configurationChangeCheckTaskObjectProvider;
    private final TaskSchedulingService taskSchedulingService;
    private final SettingRepository settingRepository;
    private final SettingState settingState;

    @Value("${application.ssh.promptforpassphrase}")
    private boolean promptForPassPhrase;

    /**
     * Creates the host management service with all required state and repository dependencies.
     */
    public HostManagementService(ComputeResourceState computeResourceState,
                                 StatusBarState statusBarProperties,
                                 ComputeResourceRepository computeResourceRepository,
                                 ObjectProvider<HostPanel> hostPanelProvider,
                                 ProgressDialog progressDialog,
                                 SshService sshService,
                                 SshState sshState,
                                 AlertUtility alertUtility,
                                 StatusBarState statusBarState,
                                 HostPanelService hostPanelService,
                                 PathFilesTableView pathFilesTableView,
                                 FilePersistenceService filePersistenceService,
                                 ObjectProvider<ConfigurationChangeCheckTask> configurationChangeCheckTaskObjectProvider,
                                 TaskSchedulingService taskSchedulingService,
                                 SettingRepository settingRepository,
                                 SettingState settingState
    )
    {
        this.computeResourceState = computeResourceState;
        this.statusBarProperties = statusBarProperties;
        this.computeResourceRepository = computeResourceRepository;
        this.hostPanelProvider = hostPanelProvider;
        this.progressDialog = progressDialog;
        this.sshService = sshService;
        this.sshState = sshState;
        this.alertUtility = alertUtility;
        this.statusBarState = statusBarState;
        this.hostPanelService = hostPanelService;
        this.pathFilesTableView = pathFilesTableView;
        this.filePersistenceService = filePersistenceService;
        this.configurationChangeCheckTaskObjectProvider = configurationChangeCheckTaskObjectProvider;
        this.taskSchedulingService = taskSchedulingService;
        this.settingRepository = settingRepository;
        this.settingState = settingState;
    }

    /**
     * Loads all compute resources from the repository and populates the UI host panels.
     */
    @Override
    public void performIntialization() {
        computeResourceState.getComputeResourcesMap()
                .addListener((MapChangeListener<Long, ComputeResource>) change -> {
                    if (change.wasRemoved() && !change.wasAdded()) {
                        computeResourceRepository.deleteById(change.getKey());
                    }
                });
        /*
         * When resources have finished loading perform
         * initial tasks.
         */
        computeResourceState.computeResourcesLoadedProperty()
                .addListener((obj, oldVal, newVal) -> {
                    if (newVal) {
                        verifyHostSshStatus();
                        LOGGER.info("Snapshot frequency: {}", settingState.getSnapshotIntervalInSeconds());
                        if (taskSchedulingService.scheduleFixedRateTask(configurationChangeCheckTaskObjectProvider.getIfAvailable(), ScheduledTaskType.CONFIGURATION_CHANGE_CHECK, settingState.getSnapshotIntervalInSeconds())) {
                            LOGGER.warn("Task previously scheduled");
                        }
                    }
                });
        pathFilesTableView.setHostManagementService(this);
        filePersistenceService.setHostManagementService(this);
    }

    /**
     * Any selected resource is deleted by this method, therefore
     * the selected hosts state property is also cleared.
     */
    public void deleteSelectedHosts() {
        statusBarProperties.selectedHostPanelListProperty()
                .get()
                .forEach(hostPanel -> {
                    ComputeResource computeResourceForPanel = computeResourceState.getHostPanelToComputeResourceMap()
                            .get(hostPanel);
                    if (isHostOnline(computeResourceForPanel)) {
                        int onlineCount = computeResourceState.getHostsOnlineCount();
                        computeResourceState.hostsOnlineCountProperty()
                                .setValue(onlineCount - 1);
                    }
                    computeResourceState.getSelectedResources()
                            .remove(computeResourceForPanel);
                    computeResourceState.getComputeResourcesMap()
                            .remove(computeResourceForPanel.getId());
                });
    }

    /**
     * Deletes either the selected host panel subset or the given source panel if no selection exists.
     *
     * @param sourcePanel the panel that triggered the delete action
     */
    public void deleteSelectedHosts(HostPanel sourcePanel) {
        ObservableList<HostPanel> selectedHosts = statusBarProperties.selectedHostPanelListProperty()
                .get();
        if (!selectedHosts.isEmpty()) {
            deleteSelectedHosts();
        } else {
            if (isHostOnline(computeResourceState.getHostPanelToComputeResourceMap()
                                     .get(sourcePanel))) {
                int onlineCount = computeResourceState.getHostsOnlineCount();
                computeResourceState.hostsOnlineCountProperty()
                        .setValue(onlineCount - 1);
            }
            ComputeResource computeResourceForPanel = computeResourceState.getHostPanelToComputeResourceMap()
                    .get(sourcePanel);
            computeResourceState.getComputeResourcesMap()
                    .remove(computeResourceForPanel.getId());
        }
    }

    /**
     * Returns whether the given compute resource currently shows an online SSH status.
     *
     * @param computeResource the resource to check
     * @return {@code true} if the host's SSH indicator is {@link com.snjdigitalsolutions.lablensfx.shapes.SshStatus#ONLINE}
     */
    private boolean isHostOnline(ComputeResource computeResource) {
        return computeResourceState.getComputeResourceHostPanelLargeMap()
                .get(computeResource.getId())
                .getStatusIndicator()
                .getHostSshStatus()
                .equals(SshStatus.ONLINE);
    }

    /**
     * Opens the host edit form pre-populated with the compute resource associated with the given panel.
     *
     * @param sourcePanel the panel whose resource should be loaded into the edit form
     */
    public void editSelectedHost(HostPanel sourcePanel) {
        ComputeResource resource = computeResourceState.getHostPanelToComputeResourceMap()
                .get(sourcePanel);
        computeResourceState.computerResourceBeingEditedProperty()
                .setValue(resource);
    }

    /**
     * Adds a new compute resource to the application state.
     *
     * @param computeResource the resource to add
     */
    public void addComputeResource(ComputeResource computeResource) {
        computeResourceState.addNewComputeResource(computeResource);
    }

    /**
     * Looks up a compute resource by its database ID.
     *
     * @param id the database ID to look up
     * @return an {@link Optional} containing the resource if found
     */
    public Optional<ComputeResource> getComputerResourceById(Long id) {
        return computeResourceRepository.findById(id);
    }

    /**
     * This is called right after the application shows and will only
     * load resources one time.
     */
    public void loadComputeResources() {
        if (!computeResourceState.computeResourcesLoadedProperty()
                .getValue()) {
            Iterable<ComputeResource> computeResources = computeResourceRepository.findAll();
            computeResources.forEach(resource -> {
                computeResourceState.getComputeResourcesMap()
                        .put(resource.getId(), resource);
            });
            Optional<Setting> optSettingInterval = settingRepository.findBySettingName(SettingType.SNAPSHOT_INTERVAL.getName());
            Optional<Setting> optSettingValue = settingRepository.findBySettingName(SettingType.SNAPSHOT_INTERVAL_VALUE.getName());
            if (optSettingInterval.isPresent() && optSettingValue.isPresent()) {
                String interval = optSettingInterval.get()
                        .getStringValue();
                String intervalValue = optSettingValue.get()
                        .getStringValue();
                switch (interval) {
                    case "Hours":
                        settingState.snapshotIntervalInSecondsProperty()
                                .set(Long.parseLong(intervalValue) * 3600L);
                        break;
                    default:
                        settingState.snapshotIntervalInSecondsProperty()
                                .set(3600L);
                }

            }
            computeResourceState.computeResourcesLoadedProperty()
                    .setValue(true);
            LOGGER.debug("Compute resources loaded");
        }
    }

    /**
     * Probes SSH connectivity for all known compute resources and updates their online-status indicators.
     */
    public void verifyHostSshStatus() {
        if (sshState.getPassPhraseMode()
                .equals(PassPhraseMode.PROVIDED) || sshState.getPassPhraseMode()
                .equals(PassPhraseMode.NOT_NEEDED)) {
            if (sshService.init()) {
                progressDialog.setProgressText("Verifying Online Status via SSH");
                SshStatusTask statusTask = new SshStatusTask(computeResourceState, progressDialog, sshService, this);
                progressDialog.setOnDialogClosed(() -> {
                    if (statusTask.isRunning()) {
                        statusTask.cancel();
                    }
                });
                progressDialog.getProgressBar()
                        .progressProperty()
                        .bind(statusTask.progressProperty());
                Thread.ofVirtual()
                        .start(statusTask);
                StageNodeBuilder.builder()
                        .setModality(Modality.APPLICATION_MODAL)
                        .setResizable(false)
                        .setTitle("SSH Status")
                        .setNode(progressDialog)
                        .buildAndShow();
            }
        } else {
            alertUtility.warningAlert("Key Passphrase", "The passphrase for key decryption has not been set.");
        }
    }

    /**
     * Probes SSH connectivity for a single compute resource identified by its database ID.
     *
     * @param resourceID the database ID of the resource to check
     */
    public void verifyHostSshStatus(Long resourceID) {
        SshStatusForSingleHostTask task = new SshStatusForSingleHostTask(resourceID, computeResourceState, sshService, this);
        Thread.ofVirtual()
                .start(task);
    }

    /**
     * Sets the SSH status indicator for the given panel to {@code UNKNOWN} and optionally decrements the online count.
     *
     * @param panel     the panel whose indicator should be reset
     * @param decrement {@code true} to decrement the online host count
     */
    public void changeHostSshStatusToUnknown(HostPanelLarge panel,
                                             boolean decrement
    )
    {
        panel.getStatusIndicator()
                .hostSshStatusProperty()
                .setValue(SshStatus.UNKNOWN);
        computeResourceState.getComputeResourceOnlineStatusMap()
                .put(panel.getComputeResourceId(), SshStatus.UNKNOWN);
        if (decrement) {
            int currentCount = computeResourceState.getHostsOnlineCount();
            if (currentCount > 0) {
                computeResourceState.hostsOnlineCountProperty()
                        .set(currentCount - 1);
            }
        }
    }

    /**
     * Creates and returns a {@link HostPanel} for every known compute resource.
     *
     * @return list of host panels, one per compute resource
     */
    public List<HostPanel> getHostPanels() {
        List<HostPanel> panels = new ArrayList<>();
        computeResourceState.getComputeResourcesMap()
                .values()
                .forEach(resource -> {
                    panels.add(createHostPanelForComputeResource(resource));
                });
        return panels;
    }

    /**
     * Instantiates and registers a host panel for the given compute resource.
     *
     * @param resource the compute resource to create a panel for
     * @return the newly created and registered {@link HostPanel}
     */
    private HostPanel createHostPanelForComputeResource(ComputeResource resource) {
        LOGGER.debug("Adding panel for resource: {}", resource.getHostName());
        HostPanel panel = hostPanelProvider.getObject();
        panel.getStyleClass()
                .add("host-panel");
        panel.setResourceModel(new ComputeResourceModel(resource));
        computeResourceState.getComputeResourceHostPanelMap()
                .put(resource.getId(), panel);
        computeResourceState.getHostPanelToComputeResourceMap()
                .put(panel, resource);
        return panel;
    }

    /**
     * Persists changes to the given compute resource and refreshes its associated host panels.
     *
     * @param resource the updated compute resource
     */
    public void updateComputeResource(ComputeResource resource) {
        LOGGER.debug(DebugUtility.getCallerInfo());
        ComputeResourceModel resourceModel = new ComputeResourceModel(resource);
        HostPanel smallPanel = computeResourceState.getComputeResourceHostPanelMap()
                .get(resource.getId());
        smallPanel.setResourceModel(resourceModel);

        HostPanelLarge largePanel = computeResourceState.getComputeResourceHostPanelLargeMap()
                .get(resource.getId());
        largePanel.setResourceModel(resourceModel);
        computeResourceState.updateComputeResource(resource);
        computeResourceState.computerResourceBeingEditedProperty()
                .setValue(null);
    }

    /**
     * Records the last successful SSH communication timestamp for the given resource.
     *
     * @param resourceID the database ID of the compute resource
     * @param value      the epoch-millis timestamp to record
     */
    public void setResourceSshCommValue(Long resourceID,
                                        Long value
    )
    {
        Optional<ComputeResource> resource = computeResourceRepository.findById(resourceID);
        resource.ifPresent(computeResource -> {
            computeResource.setSshCommunicate(value);
            computeResourceState.updateComputeResource(computeResource);
        });
    }

    /**
     * Returns all compute resources stored in the repository.
     *
     * @return an iterable of all compute resources
     */
    public Iterable<ComputeResource> getAllComputeResources() {
        return computeResourceRepository.findAll();
    }

    /**
     * Returns whether at least one compute resource has a recorded SSH communication, indicating SSH is in use.
     *
     * @return {@code true} if SSH connectivity should be established on startup
     */
    public boolean sshNeededOnStartup() {
        return computeResourceRepository.countComputeResourceBySshCommunicateIsGreaterThan(0L) > 0;
    }

    /**
     * Adds the compute resource associated with the given panel to the current selection.
     *
     * @param hostPanel the panel whose resource should be selected
     */
    public void addComputeResourceToSelectedSources(HostPanel hostPanel)
    {
        incrementsSelectedHostCount(hostPanel);
        computeResourceState.getSelectedResources()
                .add(computeResourceState.getHostPanelToComputeResourceMap()
                             .get(hostPanel));
    }

    /**
     * Increments the selected-host count and adds the given panel to the selection list.
     *
     * @param hostPanel the panel being added to the selection
     */
    private void incrementsSelectedHostCount(HostPanel hostPanel) {
        int currentValue = statusBarState.numberOfSelectedHostsProperty()
                .getValue();
        currentValue++;
        LOGGER.debug("Panel selected - {}", currentValue);
        statusBarState.numberOfSelectedHostsProperty()
                .set(currentValue);
        statusBarState.selectedHostPanelListProperty()
                .add(hostPanel);
    }

    /**
     * Removes the compute resource associated with the given panel from the current selection.
     *
     * @param hostPanel the panel whose resource should be deselected
     */
    public void removeComputeResourceFromSelectedSources(HostPanel hostPanel)
    {
        decreaseSelectedHostCount(hostPanel);
        ComputeResource resourceForPanel = computeResourceState.getHostPanelToComputeResourceMap()
                .get(hostPanel);
        computeResourceState.getSelectedResources()
                .remove(resourceForPanel);
    }

    /**
     * Decrements the selected-host count and removes the given panel from the selection list.
     *
     * @param hostPanel the panel being removed from the selection
     */
    private void decreaseSelectedHostCount(HostPanel hostPanel) {
        int currentValue = statusBarState.numberOfSelectedHostsProperty()
                .getValue();
        currentValue--;
        LOGGER.debug("Panel deselected - {}", currentValue);
        statusBarState.numberOfSelectedHostsProperty()
                .set(currentValue);
        statusBarState.selectedHostPanelListProperty()
                .remove(hostPanel);
    }

    /**
     * Returns whether at least one host is already in the selection (i.e. a multi-select is in progress).
     *
     * @return {@code true} if one or more hosts are currently selected
     */
    public boolean multipleHostsBeingSelected() {
        return statusBarState.numberOfSelectedHostsProperty()
                .intValue() >= 1;
    }

    /**
     * Replaces the current single selection with the given panel, updating styling and state.
     *
     * @param newlySelectedHostPanel the panel to make the sole selected host
     */
    public void clearCurrentlySelectedHostAndAddNewlySelectedHost(HostPanel newlySelectedHostPanel)
    {
        var currentlySelectedResource = computeResourceState.getSelectedResources()
                .getFirst();
        var currentlySelectedHostPanel = computeResourceState.getComputeResourceHostPanelMap()
                .get(currentlySelectedResource.getId());
        hostPanelService.clearSelectedStyling(currentlySelectedHostPanel);
        computeResourceState.setResourceOfHostPanelAsOnlySelection(newlySelectedHostPanel);
        statusBarState.setHostPanelAsOnlySelection(newlySelectedHostPanel);
    }

    /**
     * Returns whether at least one compute resource is currently selected.
     *
     * @return {@code true} if the selected-resources list is non-empty
     */
    public boolean isComputeResourceSelected() {
        return !computeResourceState.getSelectedResources()
                .isEmpty();
    }

    /**
     * Sets the SSH status indicator for the given resource to {@code ONLINE} and increments the online count.
     *
     * @param resource the compute resource that came online
     */
    public void setResourceStateOnline(ComputeResource resource) {
        computeResourceState.getComputeResourceHostPanelLargeMap()
                .get(resource.getId())
                .getStatusIndicator()
                .hostSshStatusProperty()
                .set(SshStatus.ONLINE);
        computeResourceState.getComputeResourceOnlineStatusMap()
                .put(resource.getId(), SshStatus.ONLINE);
        int value = computeResourceState.getHostsOnlineCount();
        computeResourceState.hostsOnlineCountProperty()
                .setValue(value + 1);
    }

    /**
     * Sets the SSH status indicator for the given resource to {@code OFFLINE} and decrements the online count.
     *
     * @param resource the compute resource that went offline
     */
    public void setResourceStateOffline(ComputeResource resource) {
        HostPanelLarge panel = computeResourceState.getComputeResourceHostPanelLargeMap()
                .get(resource.getId());
        if (panel != null) {
            panel.getStatusIndicator()
                    .hostSshStatusProperty()
                    .set(SshStatus.OFFLINE);
        }
        computeResourceState.getComputeResourceOnlineStatusMap()
                .put(resource.getId(), SshStatus.OFFLINE);
        int value = computeResourceState.getHostsOnlineCount();
        if (value > 0) {
            computeResourceState.hostsOnlineCountProperty()
                    .setValue(value - 1);
        }
    }

    /**
     * Returns the configuration paths for the currently selected resource, or an empty list if none is selected.
     *
     * @return configuration paths of the selected resource
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

}
