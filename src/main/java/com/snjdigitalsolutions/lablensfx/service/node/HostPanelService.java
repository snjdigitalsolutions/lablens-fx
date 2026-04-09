package com.snjdigitalsolutions.lablensfx.service.node;

import com.snjdigitalsolutions.lablensfx.nodes.ConfigurationPane;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.service.HostPanelStylingService;
import org.springframework.stereotype.Service;

@Service
public class HostPanelService {

    private final HostPanelStylingService hostPanelStylingService;
    private final HostManagementService hostManagementService;
    private final ConfigurationPane configurationPane;

    public HostPanelService(HostPanelStylingService hostPanelStylingService, HostManagementService hostManagementService, ConfigurationPane configurationPane) {
        this.hostPanelStylingService = hostPanelStylingService;
        this.hostManagementService = hostManagementService;
        this.configurationPane = configurationPane;
    }
}
