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

    public ListFileCommand(SshService sshService) {
        super(sshService);
    }

    @Override
    public List<String> performCommand(ComputeResource resource,
                                       String filePath,
                                       boolean elevationNeeded
    ) throws Exception
    {
        return listFiles(resource, filePath, elevationNeeded);
    }

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
