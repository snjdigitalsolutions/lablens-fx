package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CheckElevatedPrivilegesRequiredCommand extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckElevatedPrivilegesRequiredCommand.class);

    public CheckElevatedPrivilegesRequiredCommand(SshService sshService) {
        super(sshService);
    }

    public boolean checkFilePath(ComputeResource computeResource, String filePath) throws Exception {
        boolean elevationRequired = false;
        if (!filePath.isEmpty()) {
            LOGGER.debug("Hostname: {}", computeResource.getHostName());
            String response = null;
            try {
                response = executeCommand(computeResource, "test -r " + filePath + " || echo \"ELEVATION_REQUIRED\"");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (response.contains("ELEVATION_REQUIRED")) {
                elevationRequired = true;
            }
        } else {
            throw new RuntimeException("File path cannot be blank");
        }
        return elevationRequired;
    }
}
