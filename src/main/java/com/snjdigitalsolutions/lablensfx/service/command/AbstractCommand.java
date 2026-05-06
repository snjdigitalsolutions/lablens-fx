package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.SshService;

import java.util.List;

public abstract class AbstractCommand<T> implements Command<T> {

    protected final SshService sshService;

    public AbstractCommand(SshService sshService) {
        this.sshService = sshService;
    }

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

    @Override
    public T performCommand(ComputeResource resource,
                                       String filePath
    ) throws Exception
    {
        return performCommand(resource, filePath, false);
    }


}
