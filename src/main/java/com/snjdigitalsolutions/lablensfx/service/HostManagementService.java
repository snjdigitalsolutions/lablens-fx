package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.GlobalProperties;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HostManagementService implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostManagementService.class);
    private final GlobalProperties globalProperties;
    private final ComputeResourceRepository computeResourceRepository;
    private final ObjectProvider<HostPanel> hostPanelProvider;

    public HostManagementService(GlobalProperties globalProperties,
                                 ComputeResourceRepository computeResourceRepository,
                                 ObjectProvider<HostPanel> hostPanelProvider) {
        this.globalProperties = globalProperties;
        this.computeResourceRepository = computeResourceRepository;
        this.hostPanelProvider = hostPanelProvider;
    }

    @Override
    public void performIntialization() {
        globalProperties.getComputeResourcesMap().addListener((MapChangeListener<Long, ComputeResource>) change -> {
            if (change.wasRemoved()) {
                computeResourceRepository.deleteById(change.getKey());
            }
        });
    }

    public void deleteSelectedHosts() {
        globalProperties.selectedHostPanelListProperty().get().forEach(hostPanel -> {
            globalProperties.getComputeResourcesMap().remove(hostPanel.getComputeResource().getId());
        });
    }

    public void deleteSelectedHosts(HostPanel sourcePanel) {
        ObservableList<HostPanel> selectedHosts = globalProperties.selectedHostPanelListProperty().get();
        if (!selectedHosts.isEmpty()) {
            deleteSelectedHosts();
        } else {
            globalProperties.getComputeResourcesMap().remove(sourcePanel.getComputeResource().getId());
        }
    }

    public void editSelectedHost(HostPanel sourcePanel) {
        ComputeResource resource = globalProperties.getComputeResourcesMap().get(sourcePanel.getComputeResource().getId());
        globalProperties.computerResourceBeingEditedProperty().setValue(resource);
    }

    public void addComputeResource(ComputeResource computeResource) {
        computeResource = computeResourceRepository.save(computeResource);
        globalProperties.getComputeResourcesMap().put(computeResource.getId(), computeResource);
    }

    public Optional<ComputeResource> getComputerResourceById(Long id) {
        return computeResourceRepository.findById(id);
    }

    /**
     * This is called right after the application shows and will only
     * load resources completely one time.
     */
    public void loadComputeResources() {
        if (!globalProperties.computeResourcesLoadedProperty().getValue()) {
            Iterable<ComputeResource> computeResources = computeResourceRepository.findAll();
            computeResources.forEach(resource -> {
                globalProperties.getComputeResourcesMap().put(resource.getId(), resource);
            });
            globalProperties.computeResourcesLoadedProperty().setValue(true);
            LOGGER.debug("Compute resources loaded");
        }
    }

    public List<HostPanel> getHostPanels() {
        List<HostPanel> panels = new ArrayList<>();
        globalProperties.getComputeResourcesMap().values().forEach(resource -> {
            LOGGER.debug("Adding panel for resource: {}", resource.getHostName());
            HostPanel panel = hostPanelProvider.getObject();
            panel.getStyleClass().add("host-panel");
            panel.hostnameProperty().setValue(resource.getHostName());
            panel.ipAddressProperty().setValue(resource.getIpAddress());
            panel.setComputeResource(resource);
            panels.add(panel);
        });
        return panels;
    }

    public HostPanel createHostPanelForComputeResource(ComputeResource resource) {
        HostPanel panel = hostPanelProvider.getObject();
        panel.getStyleClass().add("host-panel");
        panel.hostnameProperty().setValue(resource.getHostName());
        panel.ipAddressProperty().setValue(resource.getIpAddress());
        panel.setComputeResource(resource);
        return panel;
    }

    public void updateComputeResource(ComputeResource resource) {
        resource.updateHostPanels();
        computeResourceRepository.save(resource);
        globalProperties.computerResourceBeingEditedProperty().setValue(null);
    }
}
