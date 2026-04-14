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
public class ListFileCommand extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListFileCommand.class);

    private String filePath = "";

    public ListFileCommand(SshService sshService) {
        super(sshService);
    }

    public List<String> listFiles(ComputeResource resource, String filePath, boolean elevationNeeded) throws Exception {
        this.filePath = filePath;
        List<String> fieList = new ArrayList<>();
        String command = "find " + filePath + " -type f -printf \"%T+ %m %y %f %s\n\"";
        String listingContent = "";
        if (elevationNeeded){
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
