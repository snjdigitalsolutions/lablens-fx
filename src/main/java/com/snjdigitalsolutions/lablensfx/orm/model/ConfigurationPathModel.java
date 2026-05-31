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

    /**
     * Creates an empty configuration path model.
     */
    public ConfigurationPathModel() {}

    /**
     * Creates a configuration path model populated from the given entity.
     *
     * @param configurationPath the JPA entity to copy values from
     */
    public ConfigurationPathModel(ConfigurationPath configurationPath) {
        fromConfigurationPath(configurationPath);
    }

    /**
     * Populates this model from the given {@link ConfigurationPath} entity.
     *
     * @param source the source entity
     */
    public void fromConfigurationPath(ConfigurationPath source) {
        if (source.getId() != null) id.set(source.getId());
        if (source.getComputeResource() != null && source.getComputeResource().getId() != null) {
            computeResourceId.set(source.getComputeResource().getId());
        }
        configurationPath.set(source.getConfigurationPath());
        if (source.getRequiresElevation() != null) requiresElevation.set(source.getRequiresElevation());
        if (source.getElevationCheckComplete() != null) elevationCheckComplete.set(source.getElevationCheckComplete());
    }

    /**
     * Converts this model to a {@link ConfigurationPath} entity.
     *
     * @return a new entity populated from this model's current values
     */
    public ConfigurationPath toConfigurationPath() {
        ConfigurationPath target = new ConfigurationPath();
        if (id.get() != 0) target.setId(id.get());
        target.setConfigurationPath(configurationPath.get());
        target.setRequiresElevation(requiresElevation.get());
        target.setElevationCheckComplete(elevationCheckComplete.get());
        return target;
    }

    /** @return the database ID of this configuration path */
    public long getId() { return id.get(); }
    /** @param id the database ID to set */
    public void setId(long id) { this.id.set(id); }
    /** @return the observable property for the database ID */
    public LongProperty idProperty() { return id; }

    /** @return the ID of the compute resource this path belongs to */
    public long getComputeResourceId() { return computeResourceId.get(); }
    /** @param computeResourceId the compute resource ID to set */
    public void setComputeResourceId(long computeResourceId) { this.computeResourceId.set(computeResourceId); }
    /** @return the observable property for the compute resource ID */
    public LongProperty computeResourceIdProperty() { return computeResourceId; }

    /** @return the file-system path string */
    public String getConfigurationPath() { return configurationPath.get(); }
    /** @param configurationPath the path string to set */
    public void setConfigurationPath(String configurationPath) { this.configurationPath.set(configurationPath); }
    /** @return the observable property for the configuration path string */
    public StringProperty configurationPathProperty() { return configurationPath; }

    /** @return {@code true} if this path requires elevated (sudo) privileges to access */
    public boolean isRequiresElevation() { return requiresElevation.get(); }
    /** @param requiresElevation {@code true} if elevation is required */
    public void setRequiresElevation(boolean requiresElevation) { this.requiresElevation.set(requiresElevation); }
    /** @return the observable property for the requires-elevation flag */
    public BooleanProperty requiresElevationProperty() { return requiresElevation; }

    /** @return {@code true} if the elevation check for this path has been completed */
    public boolean isElevationCheckComplete() { return elevationCheckComplete.get(); }
    /** @param elevationCheckComplete {@code true} to mark the check as complete */
    public void setElevationCheckComplete(boolean elevationCheckComplete) { this.elevationCheckComplete.set(elevationCheckComplete); }
    /** @return the observable property indicating whether the elevation check is complete */
    public BooleanProperty elevationCheckCompleteProperty() { return elevationCheckComplete; }
}
