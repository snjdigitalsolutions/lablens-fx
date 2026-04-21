package com.snjdigitalsolutions.lablensfx.orm.model;

import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConfigurationPathModel {

    private final LongProperty id = new SimpleLongProperty();
    private final LongProperty computeResourceId = new SimpleLongProperty();
    private final StringProperty configurationPath = new SimpleStringProperty();
    private final BooleanProperty requiresElevation = new SimpleBooleanProperty(false);
    private final BooleanProperty elevationCheckComplete = new SimpleBooleanProperty(false);

    public ConfigurationPathModel() {}

    public ConfigurationPathModel(ConfigurationPath configurationPath) {
        fromConfigurationPath(configurationPath);
    }

    public void fromConfigurationPath(ConfigurationPath source) {
        if (source.getId() != null) id.set(source.getId());
        if (source.getComputeResource() != null && source.getComputeResource().getId() != null) {
            computeResourceId.set(source.getComputeResource().getId());
        }
        configurationPath.set(source.getConfigurationPath());
        if (source.getRequiresElevation() != null) requiresElevation.set(source.getRequiresElevation());
        if (source.getElevationCheckComplete() != null) elevationCheckComplete.set(source.getElevationCheckComplete());
    }

    public ConfigurationPath toConfigurationPath() {
        ConfigurationPath target = new ConfigurationPath();
        if (id.get() != 0) target.setId(id.get());
        target.setConfigurationPath(configurationPath.get());
        target.setRequiresElevation(requiresElevation.get());
        target.setElevationCheckComplete(elevationCheckComplete.get());
        return target;
    }

    public long getId() { return id.get(); }
    public void setId(long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    public long getComputeResourceId() { return computeResourceId.get(); }
    public void setComputeResourceId(long computeResourceId) { this.computeResourceId.set(computeResourceId); }
    public LongProperty computeResourceIdProperty() { return computeResourceId; }

    public String getConfigurationPath() { return configurationPath.get(); }
    public void setConfigurationPath(String configurationPath) { this.configurationPath.set(configurationPath); }
    public StringProperty configurationPathProperty() { return configurationPath; }

    public boolean isRequiresElevation() { return requiresElevation.get(); }
    public void setRequiresElevation(boolean requiresElevation) { this.requiresElevation.set(requiresElevation); }
    public BooleanProperty requiresElevationProperty() { return requiresElevation; }

    public boolean isElevationCheckComplete() { return elevationCheckComplete.get(); }
    public void setElevationCheckComplete(boolean elevationCheckComplete) { this.elevationCheckComplete.set(elevationCheckComplete); }
    public BooleanProperty elevationCheckCompleteProperty() { return elevationCheckComplete; }
}
