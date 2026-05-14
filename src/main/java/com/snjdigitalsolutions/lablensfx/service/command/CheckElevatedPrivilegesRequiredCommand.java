package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CheckElevatedPrivilegesRequiredCommand extends AbstractCommand<Boolean> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckElevatedPrivilegesRequiredCommand.class);

    /**
     * Creates the elevation-check command backed by the given SSH service.
     *
     * @param sshService the SSH service used to probe the remote path
     */
    public CheckElevatedPrivilegesRequiredCommand(SshService sshService) {
        super(sshService);
    }

    /**
     * Determines whether the given path on the resource requires sudo access.
     *
     * @param resource        the target host
     * @param filePath        the file path to test
     * @param elevationNeeded unused; elevation is always probed fresh
     * @return {@code true} if the path cannot be accessed without elevated privileges
     * @throws Exception if the remote check fails
     */
    @Override
    public Boolean performCommand(ComputeResource resource,
                                  String filePath,
                                  boolean elevationNeeded
    ) throws Exception
    {
        return checkFilePath(resource, filePath);
    }

    /**
     * Probes a specific file path to determine whether read access requires elevation.
     *
     * @param computeResource the target compute resource
     * @param filePath        the absolute file path to test
     * @return {@code true} if elevation is required; {@code false} otherwise
     * @throws Exception if the remote check fails
     */
    private Boolean checkFilePath(ComputeResource computeResource, String filePath) throws Exception {
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
