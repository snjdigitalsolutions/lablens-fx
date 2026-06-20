package com.snjdigitalsolutions.lablensfx.graph;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graphview.SmartGraphEdge;
import com.snjdigitalsolutions.lablensfx.orm.FileStorage;
import com.snjdigitalsolutions.lablensfx.orm.Relational;
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
public class ChangedConfigurationGraphBase<V extends Relational> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangedConfigurationGraphBase.class);
    private final List<V> unresolvedChangedFileStorages = new ArrayList<>();

    public Digraph<V, String> getChangeGraph() {
        Map<Integer, V> idToFileStorageMap = new HashMap<>();
        Digraph<V, String> changeGraph = new DigraphEdgeList<>();
        for (V fileStorage : unresolvedChangedFileStorages) {
            changeGraph.insertVertex(fileStorage);
            idToFileStorageMap.put(fileStorage.getId()
                                           .intValue(), fileStorage);
        }
        for (V fileStorage : unresolvedChangedFileStorages) {
            if (fileStorage.getChild() != 0) {
                changeGraph.insertEdge(fileStorage, idToFileStorageMap.get(fileStorage.getChild()), "Show Differences");
            }
        }
        return changeGraph;
    }

}
