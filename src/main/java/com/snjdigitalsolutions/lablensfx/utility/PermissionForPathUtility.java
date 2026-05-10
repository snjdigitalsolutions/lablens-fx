package com.snjdigitalsolutions.lablensfx.utility;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class PermissionForPathUtility {

    public boolean pathNeedsPermissionElevated(String absolutePath, ComputeResource resource) {
        AtomicBoolean needsElevation = new AtomicBoolean(false);
        resource.getFileSystemObjects().forEach(fileSystemObject -> {
            String concatenatedPath = fileSystemObject.getParentPath() + "/" + fileSystemObject.getFileName();
            if (concatenatedPath.contentEquals(absolutePath)){
                resource.getConfigurationPaths().forEach(configurationPath -> {
                    if (configurationPath.getConfigurationPath().contentEquals(fileSystemObject.getParentPath()) && configurationPath.getRequiresElevation()){
                        needsElevation.set(true);
                    }
                });
            }
        });
        return needsElevation.get();
    }

}
