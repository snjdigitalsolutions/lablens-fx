package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.SshService;

public abstract class AbstractCommand implements Command {

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

}
