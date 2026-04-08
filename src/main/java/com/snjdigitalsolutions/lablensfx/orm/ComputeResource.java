package com.snjdigitalsolutions.lablensfx.orm;

import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.nodes.HostPanelLarge;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compute_resource")
@Getter
@Setter
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
    @Column(name = "sshport")
    private Integer sshPort;
    @Column(name = "sshcom")
    private Long sshCommunicate;
    @OneToMany(mappedBy = "computeResource", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfigurationPath> configurationPaths = new ArrayList<>();

}
