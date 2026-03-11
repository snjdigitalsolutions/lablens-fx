package com.snjdigitalsolutions.lablensfx.orm;

import jakarta.persistence.*;

@Entity
@Table(name = "compute_resource")
public class ComputeResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ipaddress")
    private String ipAddress;
    @Column(name = "os")
    private String operatingSystem;
    @Column(name = "description")
    private String description;
    @Column(name = "hostname")
    private String hostName;

}
