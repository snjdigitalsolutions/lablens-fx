package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;

public interface Command<T> {

    String executeCommand(ComputeResource computeResource, String command) throws Exception;

    String executeSudoCommand(ComputeResource computeResource, String command) throws Exception;

    /**
     * Perform the Object's command defaulting to no
     * elevation needed
     * @param resource the compute resource
     * @param filePath the absolute path on the resource
     * @param elevationNeeded boolean indicating whether
     * permission elevation is needed to perform the command
     * @return T
     */
    T performCommand(ComputeResource resource, String filePath, boolean elevationNeeded) throws Exception;

    /**
     * Perform the Object's command defaulting to no
     * elevation needed
     * @param resource the compute resource
     * @param filePath the absolute path on the resource
     * @return T
     */
    T performCommand(ComputeResource resource, String filePath) throws Exception;

}
