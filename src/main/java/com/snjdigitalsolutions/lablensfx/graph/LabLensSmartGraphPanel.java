package com.snjdigitalsolutions.lablensfx.graph;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graphview.ForceDirectedLayoutStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.snjdigitalsolutions.lablensfx.orm.Relational;

public class LabLensSmartGraphPanel<V extends Relational, E> extends SmartGraphPanel<V, E> {

    public LabLensSmartGraphPanel(Graph<V, E> theGraph,
                                  SmartPlacementStrategy placementStrategy,
                                  ForceDirectedLayoutStrategy<V> layoutStrategy
    )
    {
        super(theGraph, placementStrategy, layoutStrategy);
    }
}
