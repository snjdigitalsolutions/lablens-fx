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

    public ElevatedPrivilegedPathState(CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand) {
        this.checkElevatedPrivilegesRequiredCommand = checkElevatedPrivilegesRequiredCommand;
    }

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

    public boolean isElevationRequired(ComputeResource computeResource, String path){
        return computerToPathMap.get(computeResource).get(path);
    }

    public boolean checkElevationRequired(ComputeResource computeResource, String path) throws Exception {
        boolean elevationRequired = false;
        try {
            LOGGER.debug("Hostname: {}", computeResource.getHostName());
            elevationRequired = checkElevatedPrivilegesRequiredCommand.checkFilePath(computeResource, path);
            computerToPathMap.get(computeResource)
                    .put(path, elevationRequired);
        } catch (Exception e) {
            throw new RuntimeException("Elevation cannot be verified.");
        }
        return elevationRequired;

    }
}
