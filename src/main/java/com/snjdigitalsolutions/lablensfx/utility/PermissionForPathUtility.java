package com.snjdigitalsolutions.lablensfx.utility;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class PermissionForPathUtility {

    /**
     * Checks whether the given absolute path on the specified resource requires elevated privileges.
     *
     * @param absolutePath the absolute file path to check
     * @param resource     the compute resource (host) whose configuration paths are inspected
     * @return {@code true} if the path requires elevated access; {@code false} otherwise
     */
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
