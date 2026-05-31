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

    /**
     * Creates the MD5-sum command backed by the given SSH service.
     *
     * @param sshService the SSH service used to execute remote commands
     */
    public MD5SumCommand(SshService sshService) {
        super(sshService);
    }

    /**
     * Calculates the MD5 checksum of the file at the given path on the remote host.
     *
     * @param resource        the target host
     * @param filePath        the absolute path of the file to hash
     * @param elevationNeeded {@code true} to run with sudo
     * @return the MD5 checksum string
     * @throws Exception if the remote command fails
     */
    @Override
    public String performCommand(ComputeResource resource,
                                 String filePath,
                                 boolean elevationNeeded
    ) throws Exception
    {
        return getMd5Sum(resource, filePath, elevationNeeded);
    }

    /**
     * Runs {@code md5sum} on the remote host and returns the extracted 32-character hash.
     *
     * @param resource        the target host
     * @param filePath        the absolute file path
     * @param elevationNeeded {@code true} to use sudo
     * @return the MD5 hash string
     * @throws Exception if the remote command fails
     */
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
