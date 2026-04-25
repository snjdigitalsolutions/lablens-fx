package com.snjdigitalsolutions.lablensfx.state;

import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.nodes.HostPanelLarge;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
import com.snjdigitalsolutions.lablensfx.utility.DebugUtility;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ComputeResourceState {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputeResourceState.class);

    private final MapProperty<Long, ComputeResource> computeResourcesMap = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final MapProperty<Long, SshStatus> computeResourceOnlineStatusMap = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final MapProperty<Long, HostPanel> computeResourceHostPanelMap = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final MapProperty<HostPanel, ComputeResource> hostPanelToComputeResourceMap = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final MapProperty<Long, HostPanelLarge> computeResourceHostPanelLargeMap = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final ObjectProperty<ComputeResource> computerResourceBeingEdited = new SimpleObjectProperty<>();
    private final IntegerProperty hostsOnlineCount = new SimpleIntegerProperty(0);
    private final ListProperty<ComputeResource> selectedResources = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final BooleanProperty computeResourcesLoaded = new SimpleBooleanProperty(false);
    private final ComputeResourceRepository computeResourceRepository;

    public ComputeResourceState(ComputeResourceRepository computeResourceRepository) {
        this.computeResourceRepository = computeResourceRepository;
    }

    public ObservableMap<HostPanel, ComputeResource> getHostPanelToComputeResourceMap() {
        return hostPanelToComputeResourceMap.get();
    }

    public MapProperty<HostPanel, ComputeResource> hostPanelToComputeResourceMapProperty() {
        return hostPanelToComputeResourceMap;
    }

    public ObservableMap<Long, ComputeResource> getComputeResourcesMap() {
        return computeResourcesMap.get();
    }

    public MapProperty<Long, ComputeResource> computeResourcesMapProperty() {
        return computeResourcesMap;
    }

    public boolean isComputeResourcesLoaded() {
        return computeResourcesLoaded.get();
    }

    public BooleanProperty computeResourcesLoadedProperty() {
        return computeResourcesLoaded;
    }

    public ComputeResource getComputerResourceBeingEdited() {
        return computerResourceBeingEdited.get();
    }

    public ObjectProperty<ComputeResource> computerResourceBeingEditedProperty() {
        return computerResourceBeingEdited;
    }

    public int getHostsOnlineCount() {
        return hostsOnlineCount.get();
    }

    public IntegerProperty hostsOnlineCountProperty() {
        return hostsOnlineCount;
    }

    public ObservableMap<Long, SshStatus> getComputeResourceOnlineStatusMap() {
        return computeResourceOnlineStatusMap.get();
    }

    public MapProperty<Long, SshStatus> computeResourceOnlineStatusMapProperty() {
        return computeResourceOnlineStatusMap;
    }

    public ObservableList<ComputeResource> getSelectedResources() {
        LOGGER.debug("Size of selected resource list: {}", selectedResources.size());
        return selectedResources.get();
    }

    public ListProperty<ComputeResource> selectedResourcesProperty() {
        return selectedResources;
    }

    public ObservableMap<Long, HostPanel> getComputeResourceHostPanelMap() {
        return computeResourceHostPanelMap.get();
    }

    public MapProperty<Long, HostPanel> computeResourceHostPanelMapProperty() {
        return computeResourceHostPanelMap;
    }

    public ObservableMap<Long, HostPanelLarge> getComputeResourceHostPanelLargeMap() {
        return computeResourceHostPanelLargeMap.get();
    }

    public MapProperty<Long, HostPanelLarge> computeResourceHostPanelLargeMapProperty() {
        return computeResourceHostPanelLargeMap;
    }

    public boolean isSingleSourceSelected() {
        return selectedResources.size() == 1;
    }

    public void setResourceOfHostPanelAsOnlySelection(HostPanel panel) {
        getSelectedResources().clear();
        getSelectedResources().add(getHostPanelToComputeResourceMap().get(panel));
    }

    /**
     * Method called when a ComputeResource has been updated and
     * the database and state objects need to updated to match.
     *
     * @param computeResource the modified ComputeResource
     */
    public void updateComputeResource(ComputeResource computeResource) {
        LOGGER.debug(DebugUtility.getCallerInfo());
        boolean wasSelected = selectedResources.removeIf(r -> r.getId().equals(computeResource.getId()));
        ComputeResource savedResource = computeResourceRepository.save(computeResource);
        if (wasSelected) {
            selectedResources.add(savedResource);
        }
        computeResourcesMap.put(savedResource.getId(), savedResource);
        HostPanel mappedPanel = computeResourceHostPanelMap.get(savedResource.getId());
        hostPanelToComputeResourceMap.put(mappedPanel, savedResource);
        LOGGER.debug("Updated compute resource: {}", savedResource.getIpAddress());
    }

    public void addNewComputeResource(ComputeResource computeResource) {
        computeResource = computeResourceRepository.save(computeResource);
        getComputeResourcesMap().put(computeResource.getId(), computeResource);
    }


}
