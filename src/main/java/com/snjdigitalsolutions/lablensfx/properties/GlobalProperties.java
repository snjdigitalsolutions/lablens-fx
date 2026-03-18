package com.snjdigitalsolutions.lablensfx.properties;

import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.nodes.SummaryPanel;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.springframework.stereotype.Component;

@Component
public class GlobalProperties {

    private final ObjectProperty<SummaryPanel> numberOfHostsPanel = new SimpleObjectProperty<>();
    private final ObjectProperty<SummaryPanel> numberOfHostsOnlinePanel = new SimpleObjectProperty<>();
    private final ObjectProperty<SummaryPanel> numberOfConfigurationChangePanel = new SimpleObjectProperty<>();
    private final ObjectProperty<SummaryPanel> numberOfLogErrorsPanel = new SimpleObjectProperty<>();
    private final ObjectProperty<ComputeResource> computerResourceBeingEdited = new SimpleObjectProperty<>();
    private final ListProperty<HostPanel> selectedHostPanelList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<HostPanel> hostPanels = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final MapProperty<Long,ComputeResource> computeResourcesMap = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final BooleanProperty computeResourcesLoaded = new SimpleBooleanProperty(false);
    private final DoubleProperty largeHostPanelHeight = new SimpleDoubleProperty(90);

    public ComputeResource getComputerResourceBeingEdited() {
        return computerResourceBeingEdited.get();
    }

    public ObjectProperty<ComputeResource> computerResourceBeingEditedProperty() {
        return computerResourceBeingEdited;
    }

    public ObservableList<HostPanel> getHostPanels() {
        return hostPanels.get();
    }

    public ListProperty<HostPanel> hostPanelsProperty() {
        return hostPanels;
    }

    public SummaryPanel getNumberOfHostsPanel() {
        return numberOfHostsPanel.get();
    }

    public ObjectProperty<SummaryPanel> numberOfHostsPanelProperty() {
        return numberOfHostsPanel;
    }

    public SummaryPanel getNumberOfHostsOnlinePanel() {
        return numberOfHostsOnlinePanel.get();
    }

    public ObjectProperty<SummaryPanel> numberOfHostsOnlinePanelProperty() {
        return numberOfHostsOnlinePanel;
    }

    public SummaryPanel getNumberOfConfigurationChangePanel() {
        return numberOfConfigurationChangePanel.get();
    }

    public ObjectProperty<SummaryPanel> numberOfConfigurationChangePanelProperty() {
        return numberOfConfigurationChangePanel;
    }

    public SummaryPanel getNumberOfLogErrorsPanel() {
        return numberOfLogErrorsPanel.get();
    }

    public ObjectProperty<SummaryPanel> numberOfLogErrorsPanelProperty() {
        return numberOfLogErrorsPanel;
    }

    public ObservableList<HostPanel> getSelectedHostPanelList() {
        return selectedHostPanelList.get();
    }

    public ListProperty<HostPanel> selectedHostPanelListProperty() {
        return selectedHostPanelList;
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

    public double getLargeHostPanelHeight() {
        return largeHostPanelHeight.get();
    }

    public DoubleProperty largeHostPanelHeightProperty() {
        return largeHostPanelHeight;
    }
}
