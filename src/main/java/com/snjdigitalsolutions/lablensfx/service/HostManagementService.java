package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.nodes.*;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.ComputeResourceProperties;
import com.snjdigitalsolutions.lablensfx.properties.SshProperties;
import com.snjdigitalsolutions.lablensfx.properties.StatusBarProperties;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
import com.snjdigitalsolutions.lablensfx.task.SshStatusForSingleHostTask;
import com.snjdigitalsolutions.lablensfx.task.SshStatusTask;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.IpAddressUtility;
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
    private final ComputeResourceProperties computeResourceProperties;
    private final StatusBarProperties statusBarProperties;
    private final ComputeResourceRepository computeResourceRepository;
    private final ObjectProvider<HostPanel> hostPanelProvider;
    private final HostStatusDialog hostStatusDialog;
    private final SshService sshService;
    private final SshProperties sshProperties;
    private final PassphraseDialog passphraseDialog;

    @Value("${application.ssh.promptforpassphrase}")
    private boolean promptForPassPhrase;

    public HostManagementService(ComputeResourceProperties computeResourceProperties, StatusBarProperties statusBarProperties, ComputeResourceRepository computeResourceRepository, ObjectProvider<HostPanel> hostPanelProvider, IpAddressUtility ipAddressUtility, Environment environment, HostStatusDialog hostStatusDialog, SshService sshService, SshProperties sshProperties, PassphraseDialog passphraseDialog) {
        this.computeResourceProperties = computeResourceProperties;
        this.statusBarProperties = statusBarProperties;
        this.computeResourceRepository = computeResourceRepository;
        this.hostPanelProvider = hostPanelProvider;
        this.environment = environment;
        this.hostStatusDialog = hostStatusDialog;
        this.sshService = sshService;
        this.sshProperties = sshProperties;
        this.passphraseDialog = passphraseDialog;
    }

    @Override
    public void performIntialization() {
        computeResourceProperties.getComputeResourcesMap()
                .addListener((MapChangeListener<Long, ComputeResource>) change -> {
                    if (change.wasRemoved()) {
                        computeResourceRepository.deleteById(change.getKey());
                    }
                });
        computeResourceProperties.computeResourcesLoadedProperty()
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
                    computeResourceProperties.getComputeResourcesMap()
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
            computeResourceProperties.getComputeResourcesMap()
                    .remove(sourcePanel.getComputeResource()
                            .getId());
        }
    }

    public void editSelectedHost(HostPanel sourcePanel) {
        ComputeResource resource = computeResourceProperties.getComputeResourcesMap()
                .get(sourcePanel.getComputeResource()
                        .getId());
        computeResourceProperties.computerResourceBeingEditedProperty()
                .setValue(resource);
    }

    public void addComputeResource(ComputeResource computeResource) {
        computeResource = computeResourceRepository.save(computeResource);
        computeResourceProperties.getComputeResourcesMap()
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
        if (!computeResourceProperties.computeResourcesLoadedProperty()
                .getValue()) {
            Iterable<ComputeResource> computeResources = computeResourceRepository.findAll();
            computeResources.forEach(resource -> {
                computeResourceProperties.getComputeResourcesMap()
                        .put(resource.getId(), resource);
            });
            computeResourceProperties.computeResourcesLoadedProperty()
                    .setValue(true);
            LOGGER.debug("Compute resources loaded");
        }
    }

    public void verifyHostSshStatus() {
        Runnable postDialogAction = () -> {
            sshService.init();
            SshStatusTask statusTask = new SshStatusTask(computeResourceProperties, hostStatusDialog, sshService);
            hostStatusDialog.setOnDialogClosed(() -> {
                if (statusTask.isRunning()) {
                    statusTask.cancel();
                }
            });
            StageNodeBuilder.builder()
                    .setModality(Modality.APPLICATION_MODAL)
                    .setResizable(false)
                    .setTitle("SSH Status")
                    .setNode(hostStatusDialog)
                    .buildAndShow();
            hostStatusDialog.getStatusCheckProgressBar().progressProperty().bind(statusTask.progressProperty());
            TaskStarter.startTask(statusTask);
        };
        passphraseDialog.setPostDialogAction(postDialogAction);
        if (promptForPassPhrase) {
            StageNodeBuilder.builder()
                    .setNode(passphraseDialog)
                    .setTitle("SSH Passphrase")
                    .setModality(Modality.APPLICATION_MODAL)
                    .setResizable(false)
                    .buildAndShow();
        } else {
            //TODO when passphrase is set update is set property in property class
            sshProperties.passPhraseProperty().setValue(environment.getProperty("application.ssh.passphrase"));
            sshProperties.passPhraseSetProperty().setValue(true);
            postDialogAction.run();
        }
    }

    public void verifyHostSshStatus(Long resourceID) {
        SshStatusForSingleHostTask task = new SshStatusForSingleHostTask(resourceID, computeResourceProperties, sshService);
        TaskStarter.startTask(task);
    }

    public void changeHostSshStatusToUnknown(HostPanelLarge panel, boolean decrement){
        panel.getStatusIndicator().hostSshStatusProperty().setValue(SshStatus.UNKNOWN);
        computeResourceProperties.getComputeResourceOnlineStatusMap().put(panel.getComputeResourceId(), SshStatus.UNKNOWN);
        if (decrement) {
            int currentCount = computeResourceProperties.getHostsOnline();
            computeResourceProperties.hostsOnlineProperty()
                    .set(currentCount - 1);
        }
    }

    public List<HostPanel> getHostPanels() {
        List<HostPanel> panels = new ArrayList<>();
        computeResourceProperties.getComputeResourcesMap()
                .values()
                .forEach(resource -> {
                    LOGGER.debug("Adding panel for resource: {}", resource.getHostName());
                    HostPanel panel = hostPanelProvider.getObject();
                    panel.getStyleClass()
                            .add("host-panel");
                    panel.hostnameProperty()
                            .setValue(resource.getHostName());
                    panel.ipAddressProperty()
                            .setValue(resource.getIpAddress());
                    panel.setComputeResource(resource);
                    panels.add(panel);
                });

        //TODO create a comparator and interface for objects that have IP addresses
//        panels = ipAddressUtility.sortIpAddresses(panels);

        return panels;
    }

    public HostPanel createHostPanelForComputeResource(ComputeResource resource) {
        HostPanel panel = hostPanelProvider.getObject();
        panel.getStyleClass()
                .add("host-panel");
        panel.hostnameProperty()
                .setValue(resource.getHostName());
        panel.ipAddressProperty()
                .setValue(resource.getIpAddress());
        panel.setComputeResource(resource);
        return panel;
    }

    public void updateComputeResource(ComputeResource resource) {
        resource.updateHostPanels();
        computeResourceRepository.save(resource);
        computeResourceProperties.computerResourceBeingEditedProperty()
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
}
