package com.snjdigitalsolutions.lablensfx.graph;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.snjdigitalsolutions.lablensfx.orm.FileStorage;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the base for creating graph objects
 * for visualizing configuration file changes
 */
@Component
@Scope("prototype")
@Getter
public class ChangedConfigurationGraphBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangedConfigurationGraphBase.class);
    private final List<FileStorage> unresolvedChangedFileStorages = new ArrayList<>();

    public Digraph<FileStorage, String> getChangeGraph() {
        Map<Integer, FileStorage> idToFileStorageMap = new HashMap<>();
        Digraph<FileStorage, String> changeGraph = new DigraphEdgeList<>();
        for (FileStorage fileStorage : unresolvedChangedFileStorages) {
            changeGraph.insertVertex(fileStorage);
            idToFileStorageMap.put(fileStorage.getId()
                                           .intValue(), fileStorage);
        }
        for (FileStorage fileStorage : unresolvedChangedFileStorages) {
            if (fileStorage.getChild() != 0) {
                changeGraph.insertEdge(fileStorage, idToFileStorageMap.get(fileStorage.getChild()), "change");
            }
        }
        return changeGraph;
    }

}
