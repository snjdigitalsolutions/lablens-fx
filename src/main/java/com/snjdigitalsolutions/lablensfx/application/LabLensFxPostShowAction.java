package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.springbootutilityfx.splash.PostShowRunnable;
import org.springframework.stereotype.Component;

@Component
public class LabLensFxPostShowAction implements PostShowRunnable {

    private final HostManagementService hostManagementService;

    public LabLensFxPostShowAction(HostManagementService hostManagementService) {
        this.hostManagementService = hostManagementService;
    }

    @Override
    public void performPostSHowAction() {
        hostManagementService.loadComputeResources();
    }

}
