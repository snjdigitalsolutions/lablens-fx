package com.snjdigitalsolutions.lablensfx.orm;

import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.nodes.HostPanelLarge;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Transient
    private HostPanel hostPanel;
    @Transient
    private HostPanelLarge hostPanelLarge;
    
    public void updateHostPanels() {
        hostPanel.hostnameProperty().setValue(hostName);
        hostPanel.ipAddressProperty().setValue(ipAddress);
        hostPanelLarge.hostnameProperty().setValue(hostName);
        hostPanelLarge.ipAddressProperty().setValue(ipAddress);
        hostPanelLarge.descriptionProperty().setValue(description);
        hostPanelLarge.sshPortProperty().setValue(sshPort);
    }

}
