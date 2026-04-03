package com.snjdigitalsolutions.lablensfx.orm;

import jakarta.persistence.*;
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

}
