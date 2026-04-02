package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.properties.SshProperties;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CheckElevatedPrivilegesRequired extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckElevatedPrivilegesRequired.class);
    private String filePath = "";

    public CheckElevatedPrivilegesRequired(SshService sshService) {
        super(sshService);
    }

    @Override
    public String executeCommand(ComputeResource computeResource) throws Exception {
        if (!filePath.isEmpty()){
            String command = "test -r " + filePath + "&& ls " + filePath + "|| echo \"ELEVATION_REQUIRED\"";
            return sshService.executeCommand(computeResource.getHostName(), computeResource.getSshPort(), command);
        } else {
            throw new RuntimeException("File path cannot be blank. Use checkFilePath() to set file path and resource.");
        }
    }

    public boolean checkFilePath(ComputeResource computeResource, String path) throws Exception {
        boolean elevationRequired = false;
        this.filePath = path;
        String response = executeCommand(computeResource);
        if (response.contains("ELEVATION_REQUIRED")){
            elevationRequired = true;
        }
        return elevationRequired;
    }
}
