package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.nodes.*;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.state.SshState;
import com.snjdigitalsolutions.lablensfx.state.StatusBarState;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
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
import java.util.concurrent.atomic.AtomicBoolean;

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

    @Value("${application.ssh.promptforpassphrase}")
    private boolean promptForPassPhrase;

    public HostManagementService(ComputeResourceState computeResourceState, StatusBarState statusBarProperties, ComputeResourceRepository computeResourceRepository, ObjectProvider<HostPanel> hostPanelProvider, Environment environment, ProgressDialog progressDialog, SshService sshService, SshState sshState, PassphraseDialog passphraseDialog, AlertUtility alertUtility) {
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
    }

    @Override
    public void performIntialization() {
        computeResourceState.getComputeResourcesMap()
                .addListener((MapChangeListener<Long, ComputeResource>) change -> {
                    if (change.wasRemoved()) {
                        computeResourceRepository.deleteById(change.getKey());
                    }
                });
        computeResourceState.computeResourcesLoadedProperty()
                .addListener((obj, oldVal, newVal) -> {
                    if (newVal) {
                        verifyHostSshStatus();
                    }
                });
    }

    public void deleteSelectedHosts() {
        statusBarProperties.selectedHostPanelListProperty()
                .get()
                .forEach(hostPanel -> {
                    if (hostPanel.getComputeResource().isHostOnline()) {
                        int onlineCount = computeResourceState.getHostsOnline();
                        computeResourceState.hostsOnlineProperty().setValue(onlineCount - 1);
                    }
                    computeResourceState.getComputeResourcesMap()
                            .remove(hostPanel.getComputeResource()
                                    .getId());
                });
    }

    public void deleteSelectedHosts(HostPanel sourcePanel) {
        ObservableList<HostPanel> selectedHosts = statusBarProperties.selectedHostPanelListProperty()
                .get();
        if (!selectedHosts.isEmpty()) {
            deleteSelectedHosts();
        } else {
            if (sourcePanel.getComputeResource().isHostOnline()) {
                int onlineCount = computeResourceState.getHostsOnline();
                computeResourceState.hostsOnlineProperty().setValue(onlineCount - 1);
            }
            computeResourceState.getComputeResourcesMap()
                    .remove(sourcePanel.getComputeResource()
                            .getId());
        }
    }

    public void editSelectedHost(HostPanel sourcePanel) {
        ComputeResource resource = computeResourceState.getComputeResourcesMap()
                .get(sourcePanel.getComputeResource()
                        .getId());
        computeResourceState.computerResourceBeingEditedProperty()
                .setValue(resource);
    }

    public void addComputeResource(ComputeResource computeResource) {
        computeResource = computeResourceRepository.save(computeResource);
        computeResourceState.getComputeResourcesMap()
                .put(computeResource.getId(), computeResource);
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
        if (sshState.getPassPhraseMode().equals(PassPhraseMode.PROVIDED) ||
                sshState.getPassPhraseMode().equals(PassPhraseMode.NOT_NEEDED)){
            if(sshService.init()){
                progressDialog.setProgressText("Verifying Online Status via SSH");
                SshStatusTask statusTask = new SshStatusTask(computeResourceState, progressDialog, sshService);
                progressDialog.setOnDialogClosed(() -> {
                    if (statusTask.isRunning()) {
                        statusTask.cancel();
                    }
                });
                progressDialog.getProgressBar().progressProperty().bind(statusTask.progressProperty());
                TaskStarter.startTask(statusTask);
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
        SshStatusForSingleHostTask task = new SshStatusForSingleHostTask(resourceID, computeResourceState, sshService);
        TaskStarter.startTask(task);
    }

    public void changeHostSshStatusToUnknown(HostPanelLarge panel, boolean decrement){
        panel.getStatusIndicator().hostSshStatusProperty().setValue(SshStatus.UNKNOWN);
        computeResourceState.getComputeResourceOnlineStatusMap().put(panel.getComputeResourceId(), SshStatus.UNKNOWN);
        if (decrement) {
            int currentCount = computeResourceState.getHostsOnline();
            if (currentCount > 0) {
                computeResourceState.hostsOnlineProperty()
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

        //TODO create a comparator and interface for objects that have IP addresses
//        panels = ipAddressUtility.sortIpAddresses(panels);

        return panels;
    }

    private HostPanel createHostPanelForComputeResource(ComputeResource resource) {
        LOGGER.debug("Adding panel for resource: {}", resource.getHostName());
        HostPanel panel = hostPanelProvider.getObject();
        panel.getStyleClass()
                .add("host-panel");
        panel.hostnameProperty()
                .setValue(resource.getHostName());
        panel.ipAddressProperty()
                .setValue(resource.getIpAddress());
        panel.setComputeResource(resource);
        resource.setHostPanel(panel);
        return panel;
    }

    public void updateComputeResource(ComputeResource resource) {
        resource.updateHostPanels();
        computeResourceRepository.save(resource);
        computeResourceState.computerResourceBeingEditedProperty()
                .setValue(null);
    }

    public boolean setResourceSshCommValue(Long resourceID, Long value) {
        AtomicBoolean success = new AtomicBoolean(false);
        Optional<ComputeResource> resource = computeResourceRepository.findById(resourceID);
        resource.ifPresent(computeResource -> {
            computeResource.setSshCommunicate(value);
            computeResource = computeResourceRepository.save(computeResource);
            if (computeResource.getSshCommunicate()
                    .equals(value)) {
                success.set(true);
            }
        });
        return success.get();
    }

    public boolean sshNeededOnStartup(){
        return computeResourceRepository.countComputeResourceBySshCommunicateIsGreaterThan(0L) > 0;
    }
}
