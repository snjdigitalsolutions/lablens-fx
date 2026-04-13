package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;

public interface Command {

    String executeCommand(ComputeResource computeResource, String command) throws Exception;

}
