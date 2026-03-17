package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.nodes.HostFormPane;
import com.snjdigitalsolutions.lablensfx.nodes.HostPane;
import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.GlobalProperties;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class HostManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostManagementService.class);
    private final HostPane hostPane;
    private final HostFormPane hostFormPane;
    private final GlobalProperties globalProperties;
    private final ComputeResourceRepository computeResourceRepository;

    public HostManagementService(HostPane hostPane, HostFormPane hostFormPane, GlobalProperties globalProperties, ComputeResourceRepository computeResourceRepository) {
        this.hostPane = hostPane;
        this.hostFormPane = hostFormPane;
        this.globalProperties = globalProperties;
        this.computeResourceRepository = computeResourceRepository;
    }

    public void deleteSelectedHosts() {
        globalProperties.getSelectedHostPanelListProperty().get().forEach(hostPane::removeHostPanel);
        hostPane.refresh();
    }

    public void deleteSelectedHosts(HostPanel sourcePanel) {
        ObservableList<HostPanel> selectedHosts = globalProperties.getSelectedHostPanelListProperty().get();
        if (!selectedHosts.isEmpty()) {
            deleteSelectedHosts();
        } else {
            hostPane.removeHostPanel(sourcePanel);
            hostPane.refresh();
        }
    }

    public void editSelectedHost(HostPanel sourcePanel) {
        hostFormPane.showFormPane(sourcePanel);
    }

    public void loadComputeResources(){
        Iterable<ComputeResource> computeResources = computeResourceRepository.findAll();
        if (computeResources instanceof Collection<ComputeResource>){
            globalProperties.getComputeResourcesProperty().get().addAll((Collection<ComputeResource>) computeResources);
            globalProperties.getComputeResourcesLoadedProperty().setValue(true);
            LOGGER.debug("Compute resources loaded");
        }
    }

}
