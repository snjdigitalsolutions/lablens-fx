package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.SshService;

import java.util.List;

public abstract class AbstractCommand<T> implements Command<T> {

    protected final SshService sshService;

    /**
     * Creates an abstract command backed by the given SSH service.
     *
     * @param sshService the service used to open sessions and run commands
     */
    public AbstractCommand(SshService sshService) {
        this.sshService = sshService;
    }

    /**
     * Executes a regular (non-elevated) shell command on the given compute resource.
     *
     * @param computeResource the target host
     * @param command         the shell command to execute
     * @return the standard output of the command
     * @throws Exception if the SSH session cannot be established or the command fails
     */
    @Override
    public String executeCommand(ComputeResource computeResource,
                                 String command
    ) throws Exception
    {
        if (!sshService.init()) {
            throw new RuntimeException("SSH client not initialized");
        } else if (command != null && !command.isEmpty()) {
            return sshService.executeCommand(computeResource.getIpAddress(), computeResource.getSshPort(), command);
        } else {
            throw new RuntimeException("File path cannot be blank");
        }

    }

    /**
     * Executes a sudo-elevated shell command on the given compute resource.
     *
     * @param computeResource the target host
     * @param command         the command to run under sudo
     * @return the standard output of the command
     * @throws Exception if the SSH session cannot be established or the command fails
     */
    @Override
    public String executeSudoCommand(ComputeResource computeResource,
                                     String command
    ) throws Exception
    {
        if (!sshService.init()) {
            throw new RuntimeException("SSH client not initialized");
        } else if (command != null && !command.isEmpty()) {
            return sshService.executeSudoCommand(computeResource.getIpAddress(), computeResource.getSshPort(), command);
        } else {
            throw new RuntimeException("File path cannot be blank");
        }
    }

    /**
     * Delegates to {@link #performCommand(ComputeResource, String, boolean)} with elevation defaulting to {@code false}.
     *
     * @param resource the target host
     * @param filePath the absolute path to operate on
     * @return the command result
     * @throws Exception if the command fails
     */
    @Override
    public T performCommand(ComputeResource resource,
                                       String filePath
    ) throws Exception
    {
        return performCommand(resource, filePath, false);
    }


}
