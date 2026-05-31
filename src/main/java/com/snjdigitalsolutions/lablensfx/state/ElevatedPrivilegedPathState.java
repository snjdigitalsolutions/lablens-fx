package com.snjdigitalsolutions.lablensfx.state;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.command.CheckElevatedPrivilegesRequiredCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ElevatedPrivilegedPathState {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElevatedPrivilegedPathState.class);

    private final Map<ComputeResource, Map<String, Boolean>> computerToPathMap = new HashMap<>();
    private final CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand;

    /**
     * Creates the state bean with the command used to probe elevation requirements.
     *
     * @param checkElevatedPrivilegesRequiredCommand the command that checks whether a path needs elevated access
     */
    public ElevatedPrivilegedPathState(CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand) {
        this.checkElevatedPrivilegesRequiredCommand = checkElevatedPrivilegesRequiredCommand;
    }

    /**
     * Returns whether the elevation requirement for this path has already been determined.
     *
     * @param computeResource the host to check
     * @param path            the file path to look up
     * @return {@code true} if a cached result exists for this host and path
     */
    public boolean hasBeenChecked(ComputeResource computeResource, String path) {
        boolean checked = false;
        if (computerToPathMap.containsKey(computeResource)) {
            if (computerToPathMap.get(computeResource)
                    .containsKey(path)) {
                checked = computerToPathMap.get(computeResource)
                        .get(path);
            }
        } else {
            computerToPathMap.put(computeResource, new HashMap<>());
        }
        return checked;
    }

    /**
     * Returns the cached elevation requirement for the given host and path.
     * Call {@link #hasBeenChecked} first to confirm a result is available.
     *
     * @param computeResource the host to query
     * @param path            the file path
     * @return {@code true} if the path requires elevation; {@code false} otherwise
     */
    public boolean isElevationRequired(ComputeResource computeResource, String path){
        return computerToPathMap.get(computeResource).get(path);
    }

    /**
     * Probes the remote host to determine whether the path requires elevated access, then caches the result.
     *
     * @param computeResource the host to probe
     * @param path            the absolute path to test
     * @return {@code true} if the path requires elevation
     * @throws Exception if the remote check fails
     */
    public boolean checkElevationRequired(ComputeResource computeResource, String path) throws Exception {
        boolean elevationRequired = false;
        try {
            LOGGER.debug("Hostname: {}", computeResource.getHostName());
            elevationRequired = checkElevatedPrivilegesRequiredCommand.performCommand(computeResource, path);
            computerToPathMap.get(computeResource)
                    .put(path, elevationRequired);
        } catch (Exception e) {
            throw new RuntimeException("Elevation cannot be verified.");
        }
        return elevationRequired;

    }
}
