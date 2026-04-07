package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import org.springframework.stereotype.Service;

@Service
public class HostPanelStylingService {

    public void addSelectionStyle(HostPanel hostPanel){
        hostPanel.getStyleClass()
                .add("host-panel-selected");
    }

    public void removeSelectionStyle(HostPanel hostPanel){
        hostPanel.getStyleClass()
                .remove("host-panel-selected");
    }

}
