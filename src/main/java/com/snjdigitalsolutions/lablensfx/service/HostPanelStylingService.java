package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import org.springframework.stereotype.Service;

@Service
public class HostPanelStylingService {

    /**
     * Applies the selected-state CSS class to the given host panel.
     *
     * @param hostPanel the panel to mark as selected
     */
    public void addSelectionStyle(HostPanel hostPanel){
        hostPanel.getStyleClass()
                .add("host-panel-selected");
    }

    /**
     * Removes the selected-state CSS class from the given host panel.
     *
     * @param hostPanel the panel to deselect visually
     */
    public void removeSelectionStyle(HostPanel hostPanel){
        hostPanel.getStyleClass()
                .remove("host-panel-selected");
    }

}
