package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ListFileCommand extends AbstractCommand<List<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListFileCommand.class);

    /**
     * Creates the list-file command backed by the given SSH service.
     *
     * @param sshService the SSH service used to execute remote commands
     */
    public ListFileCommand(SshService sshService) {
        super(sshService);
    }

    /**
     * Lists files at the given path on the remote host using {@code find}.
     *
     * @param resource        the target host
     * @param filePath        the directory path to list
     * @param elevationNeeded {@code true} to run the command with sudo
     * @return a list of raw output lines from the {@code find} command
     * @throws Exception if the remote command fails
     */
    @Override
    public List<String> performCommand(ComputeResource resource,
                                       String filePath,
                                       boolean elevationNeeded
    ) throws Exception
    {
        return listFiles(resource, filePath, elevationNeeded);
    }

    /**
     * Executes the {@code find} command on the remote host and returns output lines.
     *
     * @param resource        the target host
     * @param filePath        the directory to list
     * @param elevationNeeded {@code true} to use sudo
     * @return a list of raw output lines, one per file
     * @throws Exception if the remote command fails
     */
    private List<String> listFiles(ComputeResource resource,
                                   String filePath,
                                   boolean elevationNeeded
    ) throws Exception
    {
        List<String> fieList = new ArrayList<>();
        String command = "find " + filePath + " -type f -printf \"%T+ %m %y %f %s\n\"";
        String listingContent = "";
        if (elevationNeeded) {
            listingContent = executeSudoCommand(resource, command);
        } else {
            listingContent = executeCommand(resource, command);
        }

        if (!listingContent.isEmpty()) {
            fieList.addAll(Arrays.asList(listingContent.split("\n")));
        }
        return fieList;
    }


}
