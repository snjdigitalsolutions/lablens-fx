package com.snjdigitalsolutions.lablensfx.orm;

import jakarta.persistence.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "configuration_path")
public class ConfigurationPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "compute_resource_id")
    private ComputeResource computeResource;
    @Column(name = "configuration_path")
    private String configurationPath;
    @Column(name = "requires_elevation")
    private Boolean requiresElevation;

    @Transient
    private StringProperty path = new SimpleStringProperty();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ComputeResource getComputeResource() {
        return computeResource;
    }

    public void setComputeResource(ComputeResource computeResource) {
        this.computeResource = computeResource;
    }

    public String getConfigurationPath() {
        return configurationPath;
    }

    public void setConfigurationPath(String configurationPath) {
        this.configurationPath = configurationPath;
        this.pathProperty().setValue(configurationPath);
    }

    public Boolean getRequiresElevation() {
        return requiresElevation;
    }

    public void setRequiresElevation(Boolean requiresElevation) {
        this.requiresElevation = requiresElevation;
    }

    @Transient
    public String getPath() {
        return path.get();
    }

    @Transient
    public StringProperty pathProperty() {
        return path;
    }

    @Transient
    public void setPath(String path) {
        this.path.set(path);
    }
}
