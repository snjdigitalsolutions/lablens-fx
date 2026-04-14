package com.snjdigitalsolutions.lablensfx.task;

import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.lablensfx.orm.FileSystemObject;
import com.snjdigitalsolutions.lablensfx.service.command.ListFileCommand;
import com.snjdigitalsolutions.lablensfx.service.command.commandparser.ListFileParser;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import javafx.concurrent.Task;

import java.util.List;
import java.util.function.Consumer;

public class ListFilesTask extends Task<Void> {

    private final ListFileCommand listFileCommand;
    private final ListFileParser listFileParser;
    private final ComputeResourceState computeResourceState;
    private final ConfigurationPath configurationPath;
    private final Consumer<List<FileSystemObject>> listConsumer;

    private List<FileSystemObject> fileList;

    public ListFilesTask(ListFileCommand listFileCommand,
                         ListFileParser listFileParser,
                         ComputeResourceState computeResourceState,
                         ConfigurationPath configurationPath,
                         Consumer<List<FileSystemObject>> listConsumer
    )
    {
        this.listFileCommand = listFileCommand;
        this.listFileParser = listFileParser;
        this.computeResourceState = computeResourceState;
        this.configurationPath = configurationPath;
        this.listConsumer = listConsumer;
    }

    @Override
    protected Void call() throws Exception {
        List<String> listCommandResponse = listFileCommand.listFiles(computeResourceState.getSelectedResources()
                                                                             .getFirst(), configurationPath.getConfigurationPath(), configurationPath.getRequiresElevation());
        if (listCommandResponse != null && !listCommandResponse.isEmpty()){
            fileList = listFileParser.getFileSystemObjects(configurationPath.getConfigurationPath(), listCommandResponse);
        }
        return null;
    }

    @Override
    public void succeeded(){
        listConsumer.accept(fileList);
    }

    @Override
    public void cancelled(){

    }


}
