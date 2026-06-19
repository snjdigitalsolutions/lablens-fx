package com.snjdigitalsolutions.lablensfx.graph;

import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.snjdigitalsolutions.lablensfx.orm.Relational;

public interface FileStorageHierarchicalStrategy {

    <V extends Relational, E> void placeHierarchically(double width,
                                                       double height,
                                                       SmartGraphPanel<V, E> smartGraphPanel
    );

}
