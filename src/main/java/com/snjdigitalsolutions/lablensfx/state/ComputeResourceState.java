package com.snjdigitalsolutions.lablensfx.state;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.springframework.stereotype.Component;

@Component
public class ComputeResourceState {

    private final MapProperty<Long, ComputeResource> computeResourcesMap = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final MapProperty<Long, SshStatus> computeResourceOnlineStatusMap = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final BooleanProperty computeResourcesLoaded = new SimpleBooleanProperty(false);
    private final ObjectProperty<ComputeResource> computerResourceBeingEdited = new SimpleObjectProperty<>();
    private final IntegerProperty hostsOnline = new SimpleIntegerProperty(0);
    private final ListProperty<ComputeResource> selectedResources = new SimpleListProperty<>(FXCollections.observableArrayList());

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

    public int getHostsOnline() {
        return hostsOnline.get();
    }

    public IntegerProperty hostsOnlineProperty() {
        return hostsOnline;
    }

    public ObservableMap<Long, SshStatus> getComputeResourceOnlineStatusMap() {
        return computeResourceOnlineStatusMap.get();
    }

    public MapProperty<Long, SshStatus> computeResourceOnlineStatusMapProperty() {
        return computeResourceOnlineStatusMap;
    }

    public ObservableList<ComputeResource> getSelectedResources() {
        return selectedResources.get();
    }

    public ListProperty<ComputeResource> selectedResourcesProperty() {
        return selectedResources;
    }
}
