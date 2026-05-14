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
    private final IntegerProperty configurationChangeCount = new SimpleIntegerProperty(0);
    private final ListProperty<ComputeResource> selectedResources = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final BooleanProperty computeResourcesLoaded = new SimpleBooleanProperty(false);
    private final ComputeResourceRepository computeResourceRepository;

    /**
     * Creates the state bean with the repository used to persist compute resource changes.
     *
     * @param computeResourceRepository the repository for saving and loading compute resources
     */
    public ComputeResourceState(ComputeResourceRepository computeResourceRepository) {
        this.computeResourceRepository = computeResourceRepository;
    }

    /**
     * Returns the map from host panels to their corresponding compute resources.
     *
     * @return the host-panel-to-resource map
     */
    public ObservableMap<HostPanel, ComputeResource> getHostPanelToComputeResourceMap() {
        return hostPanelToComputeResourceMap.get();
    }

    /**
     * Returns the observable map property for host-panel-to-resource associations.
     *
     * @return the host-panel-to-resource map property
     */
    public MapProperty<HostPanel, ComputeResource> hostPanelToComputeResourceMapProperty() {
        return hostPanelToComputeResourceMap;
    }

    /**
     * Returns the map from compute resource IDs to their resource objects.
     *
     * @return the compute resources map
     */
    public ObservableMap<Long, ComputeResource> getComputeResourcesMap() {
        return computeResourcesMap.get();
    }

    /**
     * Returns the observable map property for compute resources keyed by ID.
     *
     * @return the compute resources map property
     */
    public MapProperty<Long, ComputeResource> computeResourcesMapProperty() {
        return computeResourcesMap;
    }

    /**
     * Returns whether the initial compute resource load has completed.
     *
     * @return {@code true} if resources have been loaded
     */
    public boolean isComputeResourcesLoaded() {
        return computeResourcesLoaded.get();
    }

    /**
     * Returns the observable property indicating whether resources have been loaded.
     *
     * @return the compute-resources-loaded property
     */
    public BooleanProperty computeResourcesLoadedProperty() {
        return computeResourcesLoaded;
    }

    /**
     * Returns the compute resource currently open in the edit form, or {@code null} if none.
     *
     * @return the resource being edited
     */
    public ComputeResource getComputerResourceBeingEdited() {
        return computerResourceBeingEdited.get();
    }

    /**
     * Returns the observable property for the resource currently being edited.
     *
     * @return the computer-resource-being-edited property
     */
    public ObjectProperty<ComputeResource> computerResourceBeingEditedProperty() {
        return computerResourceBeingEdited;
    }

    /**
     * Returns the count of hosts whose SSH status is currently online.
     *
     * @return the online host count
     */
    public int getHostsOnlineCount() {
        return hostsOnlineCount.get();
    }

    /**
     * Returns the observable integer property for the online host count.
     *
     * @return the hosts-online-count property
     */
    public IntegerProperty hostsOnlineCountProperty() {
        return hostsOnlineCount;
    }

    /**
     * Returns the map from compute resource ID to its current SSH online status.
     *
     * @return the online-status map
     */
    public ObservableMap<Long, SshStatus> getComputeResourceOnlineStatusMap() {
        return computeResourceOnlineStatusMap.get();
    }

    /**
     * Returns the observable map property for per-resource online status.
     *
     * @return the online-status map property
     */
    public MapProperty<Long, SshStatus> computeResourceOnlineStatusMapProperty() {
        return computeResourceOnlineStatusMap;
    }

    /**
     * Returns the list of currently selected compute resources.
     *
     * @return the selected resources list
     */
    public ObservableList<ComputeResource> getSelectedResources() {
        LOGGER.debug("Size of selected resource list: {}", selectedResources.size());
        return selectedResources.get();
    }

    /**
     * Returns the observable list property of currently selected resources.
     *
     * @return the selected-resources property
     */
    public ListProperty<ComputeResource> selectedResourcesProperty() {
        return selectedResources;
    }

    /**
     * Returns the map from compute resource IDs to their associated small host panels.
     *
     * @return the resource-to-panel map
     */
    public ObservableMap<Long, HostPanel> getComputeResourceHostPanelMap() {
        return computeResourceHostPanelMap.get();
    }

    /**
     * Returns the observable map property for resource-to-panel associations.
     *
     * @return the resource-to-panel map property
     */
    public MapProperty<Long, HostPanel> computeResourceHostPanelMapProperty() {
        return computeResourceHostPanelMap;
    }

    /**
     * Returns the map from compute resource IDs to their associated large host panels.
     *
     * @return the resource-to-large-panel map
     */
    public ObservableMap<Long, HostPanelLarge> getComputeResourceHostPanelLargeMap() {
        return computeResourceHostPanelLargeMap.get();
    }

    /**
     * Returns the observable map property for resource-to-large-panel associations.
     *
     * @return the resource-to-large-panel map property
     */
    public MapProperty<Long, HostPanelLarge> computeResourceHostPanelLargeMapProperty() {
        return computeResourceHostPanelLargeMap;
    }

    /**
     * Returns whether exactly one compute resource is currently selected.
     *
     * @return {@code true} if the selected-resources list has exactly one entry
     */
    public boolean isSingleSourceSelected() {
        return selectedResources.size() == 1;
    }

    /**
     * Clears the current selection and sets the resource associated with the given panel as the sole selection.
     *
     * @param panel the panel whose resource should become the only selected resource
     */
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

    /**
     * Persists and registers a new compute resource in the application state.
     *
     * @param computeResource the resource to save and add to the state maps
     */
    public void addNewComputeResource(ComputeResource computeResource) {
        computeResource = computeResourceRepository.save(computeResource);
        getComputeResourcesMap().put(computeResource.getId(), computeResource);
    }

    /**
     * Returns the count of tracked configuration files that have changed since the last check.
     *
     * @return the configuration change count
     */
    public int getConfigurationChangeCount() {
        return configurationChangeCount.get();
    }

    /**
     * Returns the observable integer property for the configuration change count.
     *
     * @return the configuration-change-count property
     */
    public IntegerProperty configurationChangeCountProperty() {
        return configurationChangeCount;
    }
}
