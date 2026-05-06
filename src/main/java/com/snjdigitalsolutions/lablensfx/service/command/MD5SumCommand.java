package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import org.springframework.stereotype.Component;

/**
 * This command is used to perform a file hash on a compute
 * resource. This is useful when verifying file changes
 * compared to the database by comparing the hash on the
 * system with the hash in the database
 */
@Component
public class MD5SumCommand extends AbstractCommand<String> {

    public MD5SumCommand(SshService sshService) {
        super(sshService);
    }

    @Override
    public String performCommand(ComputeResource resource,
                                 String filePath,
                                 boolean elevationNeeded
    ) throws Exception
    {
        return getMd5Sum(resource, filePath, elevationNeeded);
    }

    private String getMd5Sum(ComputeResource resource,
                             String filePath,
                             boolean elevationNeeded
    ) throws Exception
    {
        String md5Sum = "";
        String command = "md5sum " + filePath;
        if (elevationNeeded) {
            md5Sum = executeSudoCommand(resource, command);
        } else {
            md5Sum = executeCommand(resource, command);
        }
        if (!md5Sum.isEmpty()) {
            md5Sum = md5Sum.substring(0, 32);
        }
        return md5Sum;
    }
}
