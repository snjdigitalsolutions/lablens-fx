package com.snjdigitalsolutions.lablensfx.orm;

import jakarta.persistence.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "configuration_path")
@Getter
@Setter
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
    public StringProperty configurationPath() {
        return new SimpleStringProperty(configurationPath);
    }

    @Transient
    public BooleanProperty requiresElevation() {
        return new SimpleBooleanProperty(requiresElevation);
    }

}
