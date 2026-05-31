package com.snjdigitalsolutions.lablensfx.service.node;

import com.snjdigitalsolutions.lablensfx.nodes.ConfigurationPane;
import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.service.HostPanelStylingService;
import org.springframework.stereotype.Service;

@Service
public class HostPanelService {

    private final HostPanelStylingService hostPanelStylingService;
    private final ConfigurationPaneService configurationPaneService;

    /**
     * Creates the host panel service with styling and state dependencies.
     */
    public HostPanelService(HostPanelStylingService hostPanelStylingService,
                            ConfigurationPaneService configurationPaneService
    ) {
        this.hostPanelStylingService = hostPanelStylingService;
        this.configurationPaneService = configurationPaneService;
    }

    /**
     * When a host panel is selected and a new selection is made
     * and the user confirms they do not want to go back to the
     * dashboard view.
     *
     * @param panel the newly selected panel
     */
    public void changeSelectedHostPanel(HostPanel panel) {
        hostPanelStylingService.addSelectionStyle(panel);
        panel.setSelectionState(true);
        configurationPaneService.loadExistingPaths();
    }

    /**
     * Applies selection styling to the given panel.
     *
     * @param panel the host panel to mark as selected
     */
    public void setHostPanelSelected(HostPanel panel) {
        panel.setSelectionState(true);
        hostPanelStylingService.addSelectionStyle(panel);
    }

    /**
     * Removes selection styling from the given panel.
     *
     * @param panel the host panel to deselect
     */
    public void clearSelectedStyling(HostPanel panel) {
        panel.setSelectionState(false);
        hostPanelStylingService.removeSelectionStyle(panel);
    }
}
