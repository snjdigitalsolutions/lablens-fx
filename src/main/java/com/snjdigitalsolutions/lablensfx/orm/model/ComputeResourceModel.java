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

    public ComputeResourceModel() {}

    public ComputeResourceModel(ComputeResource computeResource) {
        fromComputeResource(computeResource);
    }

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

    public long getId() { return id.get(); }
    public void setId(long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    public String getIpAddress() { return ipAddress.get(); }
    public void setIpAddress(String ipAddress) { this.ipAddress.set(ipAddress); }
    public StringProperty ipAddressProperty() { return ipAddress; }

    public String getOperatingSystem() { return operatingSystem.get(); }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem.set(operatingSystem); }
    public StringProperty operatingSystemProperty() { return operatingSystem; }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    public String getHostName() { return hostName.get(); }
    public void setHostName(String hostName) { this.hostName.set(hostName); }
    public StringProperty hostNameProperty() { return hostName; }

    public Integer getSshPort() { return sshPort.get(); }
    public void setSshPort(Integer sshPort) { this.sshPort.set(sshPort); }
    public ObjectProperty<Integer> sshPortProperty() { return sshPort; }

    public Long getSshCommunicate() { return sshCommunicate.get(); }
    public void setSshCommunicate(Long sshCommunicate) { this.sshCommunicate.set(sshCommunicate); }
    public ObjectProperty<Long> sshCommunicateProperty() { return sshCommunicate; }

    public ObservableList<ConfigurationPathModel> getConfigurationPaths() { return configurationPaths; }
}
