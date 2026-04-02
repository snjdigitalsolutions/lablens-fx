package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.service.SshService;

public abstract class AbstractCommand implements Command {

    protected final SshService sshService;

    public AbstractCommand(SshService sshService) {
        this.sshService = sshService;
    }

}
