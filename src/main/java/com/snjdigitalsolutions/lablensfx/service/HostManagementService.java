package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.ComputeResourceProperties;
import com.snjdigitalsolutions.lablensfx.properties.StatusBarProperties;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.IpAddressUtility;
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
    private final ComputeResourceProperties computeResourceProperties;
    private final StatusBarProperties statusBarProperties;
    private final ComputeResourceRepository computeResourceRepository;
    private final ObjectProvider<HostPanel> hostPanelProvider;
    private final IpAddressUtility ipAddressUtility;

    public HostManagementService(ComputeResourceProperties computeResourceProperties,
                                 StatusBarProperties statusBarProperties,
                                 ComputeResourceRepository computeResourceRepository,
                                 ObjectProvider<HostPanel> hostPanelProvider,
                                 IpAddressUtility ipAddressUtility) {
        this.computeResourceProperties = computeResourceProperties;
        this.statusBarProperties = statusBarProperties;
        this.computeResourceRepository = computeResourceRepository;
        this.hostPanelProvider = hostPanelProvider;
        this.ipAddressUtility = ipAddressUtility;
    }

    @Override
    public void performIntialization() {
        computeResourceProperties.getComputeResourcesMap().addListener((MapChangeListener<Long, ComputeResource>) change -> {
            if (change.wasRemoved()) {
                computeResourceRepository.deleteById(change.getKey());
            }
        });
    }

    public void deleteSelectedHosts() {
        statusBarProperties.selectedHostPanelListProperty().get().forEach(hostPanel -> {
            computeResourceProperties.getComputeResourcesMap().remove(hostPanel.getComputeResource().getId());
        });
    }

    public void deleteSelectedHosts(HostPanel sourcePanel) {
        ObservableList<HostPanel> selectedHosts = statusBarProperties.selectedHostPanelListProperty().get();
        if (!selectedHosts.isEmpty()) {
            deleteSelectedHosts();
        } else {
            computeResourceProperties.getComputeResourcesMap().remove(sourcePanel.getComputeResource().getId());
        }
    }

    public void editSelectedHost(HostPanel sourcePanel) {
        ComputeResource resource = computeResourceProperties.getComputeResourcesMap().get(sourcePanel.getComputeResource().getId());
        computeResourceProperties.computerResourceBeingEditedProperty().setValue(resource);
    }

    public void addComputeResource(ComputeResource computeResource) {
        computeResource = computeResourceRepository.save(computeResource);
        computeResourceProperties.getComputeResourcesMap().put(computeResource.getId(), computeResource);
    }

    public Optional<ComputeResource> getComputerResourceById(Long id) {
        return computeResourceRepository.findById(id);
    }

    /**
     * This is called right after the application shows and will only
     * load resources completely one time.
     */
    public void loadComputeResources() {
        if (!computeResourceProperties.computeResourcesLoadedProperty().getValue()) {
            Iterable<ComputeResource> computeResources = computeResourceRepository.findAll();
            computeResources.forEach(resource -> {
                computeResourceProperties.getComputeResourcesMap().put(resource.getId(), resource);
            });
            computeResourceProperties.computeResourcesLoadedProperty().setValue(true);
            LOGGER.debug("Compute resources loaded");
        }
    }

    public List<HostPanel> getHostPanels() {
        List<HostPanel> panels = new ArrayList<>();
        computeResourceProperties.getComputeResourcesMap().values().forEach(resource -> {
            LOGGER.debug("Adding panel for resource: {}", resource.getHostName());
            HostPanel panel = hostPanelProvider.getObject();
            panel.getStyleClass().add("host-panel");
            panel.hostnameProperty().setValue(resource.getHostName());
            panel.ipAddressProperty().setValue(resource.getIpAddress());
            panel.setComputeResource(resource);
            panels.add(panel);
        });

        //TODO create a comparator and interface for objects that have IP addresses
//        panels = ipAddressUtility.sortIpAddresses(panels);

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
        computeResourceProperties.computerResourceBeingEditedProperty().setValue(null);
    }
}
