package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ElevatedPrivilegedPathTracker {

    private final Map<ComputeResource, Map<String, Boolean>> computerToPathMap = new HashMap<>();

    private final CheckElevatedPrivilegesRequired checkElevatedPrivilegesRequired;

    public ElevatedPrivilegedPathTracker(CheckElevatedPrivilegesRequired checkElevatedPrivilegesRequired) {
        this.checkElevatedPrivilegesRequired = checkElevatedPrivilegesRequired;
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
            elevationRequired = checkElevatedPrivilegesRequired.checkFilePath(computeResource, path);
            computerToPathMap.get(computeResource)
                    .put(path, elevationRequired);
        } catch (Exception e) {
            throw new RuntimeException("Elevation cannot be verified.");
        }
        return elevationRequired;

    }
}
