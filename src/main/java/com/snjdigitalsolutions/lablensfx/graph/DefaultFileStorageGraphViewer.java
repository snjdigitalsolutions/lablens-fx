package com.snjdigitalsolutions.lablensfx.graph;

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graphview.*;
import com.snjdigitalsolutions.lablensfx.orm.FileStorage;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultFileStorageGraphViewer implements GraphViewer<FileStorage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFileStorageGraphViewer.class);
    private final ChangedConfigurationGraphBaseCreator changedConfigurationGraphBaseCreator;
    private final HierarchicalPlacementStrategy hierarchicalPlacementStrategy;

    public DefaultFileStorageGraphViewer(ChangedConfigurationGraphBaseCreator changedConfigurationGraphBaseCreator,
                                         HierarchicalPlacementStrategy hierarchicalPlacementStrategy
    ) {
        this.changedConfigurationGraphBaseCreator = changedConfigurationGraphBaseCreator;
        this.hierarchicalPlacementStrategy = hierarchicalPlacementStrategy;
    }

    @Override
    public void showGraph(String labelText) {
        LOGGER.info("Number of changed configuration files: {}", Integer.parseInt(labelText));
        List<ChangedConfigurationGraphBase<FileStorage>> changedConfigurations = changedConfigurationGraphBaseCreator.createChangedConfigurationGraphBase();
        for (ChangedConfigurationGraphBase<FileStorage> changedConfigurationGraphBase : changedConfigurations) {
            Digraph<FileStorage, String> graph = changedConfigurationGraphBase.getChangeGraph();

            // using for PoC only
//            SmartPlacementStrategy initialPlacement = new SmartCircularSortedPlacementStrategy();
            ForceDirectedLayoutStrategy<FileStorage> automaticPlacementStrategy = new ForceDirectedSpringGravityLayoutStrategy<>();
            SmartGraphPanel<FileStorage, String> graphView = new LabLensSmartGraphPanel<>(graph, hierarchicalPlacementStrategy, automaticPlacementStrategy);
            Scene scene = new Scene(new SmartGraphDemoContainer(graphView), 1024, 768);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("JavaFX SmartGraph Visualization");
            stage.setMinHeight(500);
            stage.setMinWidth(800);
            stage.setScene(scene);
            stage.show();

            graphView.init();

            LOGGER.debug("Inspecting graph");
        }
        LOGGER.debug("Changed configurations created");
    }
}
