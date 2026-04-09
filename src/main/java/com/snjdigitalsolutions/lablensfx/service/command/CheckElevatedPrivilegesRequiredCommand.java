package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CheckElevatedPrivilegesRequiredCommand extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckElevatedPrivilegesRequiredCommand.class);
    private String filePath = "";

    public CheckElevatedPrivilegesRequiredCommand(SshService sshService) {
        super(sshService);
    }

    @Override
    public String executeCommand(ComputeResource computeResource) throws Exception {
        if (!filePath.isEmpty()) {
            String command = "test -r " + filePath + " || echo \"ELEVATION_REQUIRED\"";
            return sshService.executeCommand(computeResource.getIpAddress(), computeResource.getSshPort(), command);
        } else {
            throw new RuntimeException("File path cannot be blank. Use checkFilePath() to set file path and resource.");
        }
    }

    public boolean checkFilePath(ComputeResource computeResource, String path) throws Exception {
        boolean elevationRequired = false;
        this.filePath = path;
        String response = null;
        try {
            response = executeCommand(computeResource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (response.contains("ELEVATION_REQUIRED")) {
            elevationRequired = true;
        }
        return elevationRequired;
    }
}
