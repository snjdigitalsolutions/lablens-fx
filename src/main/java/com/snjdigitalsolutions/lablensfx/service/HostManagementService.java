package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.nodes.HostPanelLarge;
import com.snjdigitalsolutions.lablensfx.nodes.PassphraseDialog;
import com.snjdigitalsolutions.lablensfx.nodes.ProgressDialog;
import com.snjdigitalsolutions.lablensfx.nodes.tableview.PathFilesTableView;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.lablensfx.orm.model.ComputeResourceModel;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.lablensfx.service.node.HostPanelService;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.state.SshState;
import com.snjdigitalsolutions.lablensfx.state.StatusBarState;
import com.snjdigitalsolutions.lablensfx.task.SshStatusForSingleHostTask;
import com.snjdigitalsolutions.lablensfx.task.SshStatusTask;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.StageNodeBuilder;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.TaskStarter;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HostManagementService implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostManagementService.class);
    private final Environment environment;
    private final ComputeResourceState computeResourceState;
    private final StatusBarState statusBarProperties;
    private final ComputeResourceRepository computeResourceRepository;
    private final ObjectProvider<HostPanel> hostPanelProvider;
    private final ProgressDialog progressDialog;
    private final SshService sshService;
    private final SshState sshState;
    private final PassphraseDialog passphraseDialog;
    private final AlertUtility alertUtility;
    private final StatusBarState statusBarState;
    private final HostPanelStylingService hostPanelStylingService;
    private final HostPanelService hostPanelService;
    private final PathFilesTableView pathFilesTableView;

    @Value("${application.ssh.promptforpassphrase}")
    private boolean promptForPassPhrase;

    public HostManagementService(ComputeResourceState computeResourceState,
                                 StatusBarState statusBarProperties,
                                 ComputeResourceRepository computeResourceRepository,
                                 ObjectProvider<HostPanel> hostPanelProvider,
                                 Environment environment,
                                 ProgressDialog progressDialog,
                                 SshService sshService,
                                 SshState sshState,
                                 PassphraseDialog passphraseDialog,
                                 AlertUtility alertUtility,
                                 StatusBarState statusBarState,
                                 HostPanelStylingService hostPanelStylingService,
                                 HostPanelService hostPanelService,
                                 PathFilesTableView pathFilesTableView
    )
    {
        this.computeResourceState = computeResourceState;
        this.statusBarProperties = statusBarProperties;
        this.computeResourceRepository = computeResourceRepository;
        this.hostPanelProvider = hostPanelProvider;
        this.environment = environment;
        this.progressDialog = progressDialog;
        this.sshService = sshService;
        this.sshState = sshState;
        this.passphraseDialog = passphraseDialog;
        this.alertUtility = alertUtility;
        this.statusBarState = statusBarState;
        this.hostPanelStylingService = hostPanelStylingService;
        this.hostPanelService = hostPanelService;
        this.pathFilesTableView = pathFilesTableView;
    }

    @Override
    public void performIntialization() {
        computeResourceState.getComputeResourcesMap()
                .addListener((MapChangeListener<Long, ComputeResource>) change -> {
                    if (change.wasRemoved() && !change.wasAdded()) {
                        computeResourceRepository.deleteById(change.getKey());
                    }
                });
        computeResourceState.computeResourcesLoadedProperty()
                .addListener((obj, oldVal, newVal) -> {
                    if (newVal) {
                        verifyHostSshStatus();
                    }
                });
        pathFilesTableView.setHostManagementService(this);
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

    private boolean isHostOnline(ComputeResource computeResource) {
        return computeResourceState.getComputeResourceHostPanelLargeMap()
                .get(computeResource.getId())
                .getStatusIndicator()
                .getHostSshStatus()
                .equals(SshStatus.ONLINE);
    }

    public void editSelectedHost(HostPanel sourcePanel) {
        ComputeResource resource = computeResourceState.getHostPanelToComputeResourceMap()
                .get(sourcePanel);
        computeResourceState.computerResourceBeingEditedProperty()
                .setValue(resource);
    }

    public void addComputeResource(ComputeResource computeResource) {
        computeResourceState.addNewComputeResource(computeResource);
    }

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
            computeResourceState.computeResourcesLoadedProperty()
                    .setValue(true);
            LOGGER.debug("Compute resources loaded");
        }
    }

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
                Thread.ofVirtual().start(statusTask);
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

    public void verifyHostSshStatus(Long resourceID) {
        SshStatusForSingleHostTask task = new SshStatusForSingleHostTask(resourceID, computeResourceState, sshService, this);
        Thread.ofVirtual().start(task);
    }

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

    public List<HostPanel> getHostPanels() {
        List<HostPanel> panels = new ArrayList<>();
        computeResourceState.getComputeResourcesMap()
                .values()
                .forEach(resource -> {
                    panels.add(createHostPanelForComputeResource(resource));
                });
        return panels;
    }

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

    public void updateComputeResource(ComputeResource resource) {
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

    public boolean sshNeededOnStartup() {
        return computeResourceRepository.countComputeResourceBySshCommunicateIsGreaterThan(0L) > 0;
    }

    public void addComputeResourceToSelectedSources(HostPanel hostPanel)
    {
        incrementsSelectedHostCount(hostPanel);
        computeResourceState.getSelectedResources()
                .add(computeResourceState.getHostPanelToComputeResourceMap()
                             .get(hostPanel));
    }

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

    public void removeComputeResourceFromSelectedSources(HostPanel hostPanel)
    {
        decreaseSelectedHostCount(hostPanel);
        ComputeResource resourceForPanel = computeResourceState.getHostPanelToComputeResourceMap()
                .get(hostPanel);
        computeResourceState.getSelectedResources()
                .remove(resourceForPanel);
    }

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

    public boolean multipleHostsBeingSelected() {
        return statusBarState.numberOfSelectedHostsProperty()
                .intValue() >= 1;
    }

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

    public boolean isComputeResourceSelected() {
        return !computeResourceState.getSelectedResources()
                .isEmpty();
    }

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
