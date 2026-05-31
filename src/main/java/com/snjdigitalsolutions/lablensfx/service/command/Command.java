package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;

public interface Command<T> {

    /**
     * Executes a shell command on the given compute resource and returns the standard output.
     *
     * @param computeResource the target host
     * @param command         the shell command to execute
     * @return the command's standard output
     * @throws Exception if the session cannot be established or the command fails
     */
    String executeCommand(ComputeResource computeResource, String command) throws Exception;

    /**
     * Executes a sudo-elevated shell command on the given compute resource and returns the standard output.
     *
     * @param computeResource the target host
     * @param command         the command to run under sudo
     * @return the command's standard output
     * @throws Exception if the session cannot be established or the command fails
     */
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
