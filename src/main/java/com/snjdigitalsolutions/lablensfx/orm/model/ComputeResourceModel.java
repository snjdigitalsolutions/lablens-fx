package com.snjdigitalsolutions.lablensfx.orm.model;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

public class ComputeResourceModel {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty ipAddress = new SimpleStringProperty();
    private final StringProperty operatingSystem = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty hostName = new SimpleStringProperty();
    private final ObjectProperty<Integer> sshPort = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> sshCommunicate = new SimpleObjectProperty<>();
    private final ObservableList<ConfigurationPathModel> configurationPaths = FXCollections.observableArrayList();

    /**
     * Creates an empty compute resource model with default observable property values.
     */
    public ComputeResourceModel() {}

    /**
     * Creates a compute resource model pre-populated from the given entity.
     *
     * @param computeResource the JPA entity to copy values from
     */
    public ComputeResourceModel(ComputeResource computeResource) {
        fromComputeResource(computeResource);
    }

    /**
     * Populates this model's properties from the given {@link ComputeResource} entity.
     *
     * @param source the source entity
     */
    public void fromComputeResource(ComputeResource source) {
        if (source.getId() != null) id.set(source.getId());
        ipAddress.set(source.getIpAddress());
        operatingSystem.set(source.getOperatingSystem());
        description.set(source.getDescription());
        hostName.set(source.getHostName());
        sshPort.set(source.getSshPort());
        sshCommunicate.set(source.getSshCommunicate());
        configurationPaths.setAll(
                source.getConfigurationPaths().stream()
                        .map(ConfigurationPathModel::new)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Converts this model back into a {@link ComputeResource} JPA entity.
     *
     * @return a new entity populated with this model's current values
     */
    public ComputeResource toComputeResource() {
        ComputeResource target = new ComputeResource();
        if (id.get() != 0) target.setId(id.get());
        target.setIpAddress(ipAddress.get());
        target.setOperatingSystem(operatingSystem.get());
        target.setDescription(description.get());
        target.setHostName(hostName.get());
        target.setSshPort(sshPort.get());
        target.setSshCommunicate(sshCommunicate.get());
        return target;
    }

    /** @return the database ID of this compute resource */
    public long getId() { return id.get(); }
    /** @param id the database ID to set */
    public void setId(long id) { this.id.set(id); }
    /** @return the observable property for the database ID */
    public LongProperty idProperty() { return id; }

    /** @return the IP address of this compute resource */
    public String getIpAddress() { return ipAddress.get(); }
    /** @param ipAddress the IP address to set */
    public void setIpAddress(String ipAddress) { this.ipAddress.set(ipAddress); }
    /** @return the observable property for the IP address */
    public StringProperty ipAddressProperty() { return ipAddress; }

    /** @return the operating system name of this compute resource */
    public String getOperatingSystem() { return operatingSystem.get(); }
    /** @param operatingSystem the OS name to set */
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem.set(operatingSystem); }
    /** @return the observable property for the operating system name */
    public StringProperty operatingSystemProperty() { return operatingSystem; }

    /** @return the description of this compute resource */
    public String getDescription() { return description.get(); }
    /** @param description the description to set */
    public void setDescription(String description) { this.description.set(description); }
    /** @return the observable property for the description */
    public StringProperty descriptionProperty() { return description; }

    /** @return the hostname of this compute resource */
    public String getHostName() { return hostName.get(); }
    /** @param hostName the hostname to set */
    public void setHostName(String hostName) { this.hostName.set(hostName); }
    /** @return the observable property for the hostname */
    public StringProperty hostNameProperty() { return hostName; }

    /** @return the SSH port used to connect to this compute resource */
    public Integer getSshPort() { return sshPort.get(); }
    /** @param sshPort the SSH port number to set */
    public void setSshPort(Integer sshPort) { this.sshPort.set(sshPort); }
    /** @return the observable property for the SSH port */
    public ObjectProperty<Integer> sshPortProperty() { return sshPort; }

    /** @return the timestamp of the last successful SSH communication with this resource */
    public Long getSshCommunicate() { return sshCommunicate.get(); }
    /** @param sshCommunicate the epoch-millis timestamp to set */
    public void setSshCommunicate(Long sshCommunicate) { this.sshCommunicate.set(sshCommunicate); }
    /** @return the observable property for the last SSH communication timestamp */
    public ObjectProperty<Long> sshCommunicateProperty() { return sshCommunicate; }

    /** @return the configuration path models associated with this compute resource */
    public ObservableList<ConfigurationPathModel> getConfigurationPaths() { return configurationPaths; }
}
