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

    @Override
    public String executeCommand(ComputeResource computeResource) throws Exception {
        if (!filePath.isEmpty()){

            String command = "find " + filePath + " -type f -printf \"%T+ %m %y %f %s\n\"";
            return sshService.executeCommand(computeResource.getHostName(), computeResource.getSshPort(), command);
        } else {
            throw new RuntimeException("File path cannot be blank. Use listFiles() to set file path and resource.");
        }
    }

    public List<String> listFiles(ComputeResource resource, String filePath) throws Exception {
        this.filePath = filePath;
        List<String> fieList = new ArrayList<>();
        String listingContent = executeCommand(resource);
        if (!listingContent.isEmpty()) {
            fieList.addAll(Arrays.asList(listingContent.split("\n")));
        }
        return fieList;
    }
}
