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

import java.time.Instant;
import java.time.format.DateTimeFormatter;
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
        try {
            LOGGER.info("Number of changed configuration files: {}", Integer.parseInt(labelText));
        } catch (NumberFormatException ex) {
            LOGGER.warn("Invalid number format for changed configuration files label: '{}'", labelText, ex);
        }
        List<ChangedConfigurationGraphBase<FileStorage>> changedConfigurations = changedConfigurationGraphBaseCreator.createChangedConfigurationGraphBase();
        for (ChangedConfigurationGraphBase<FileStorage> changedConfigurationGraphBase : changedConfigurations) {
            Digraph<FileStorage, String> graph = changedConfigurationGraphBase.getChangeGraph();

            // TODO using for PoC only
            ForceDirectedLayoutStrategy<FileStorage> automaticPlacementStrategy = new ForceDirectedSpringGravityLayoutStrategy<>();
            SmartGraphPanel<FileStorage, String> graphView = new LabLensSmartGraphPanel<>(graph, hierarchicalPlacementStrategy, automaticPlacementStrategy);
            for (SmartGraphEdge<String,FileStorage> smartEdge : graphView.getSmartEdges()){
                SmartStylableNode label = smartEdge.getStylableLabel();
                if (label instanceof SmartLabel smartLabel) {
                    smartLabel.setOnMouseClicked(event -> {
                        SmartGraphVertex<FileStorage> inboundEdge = smartEdge.getInbound();
                        SmartGraphVertex<FileStorage> outboundEdge = smartEdge.getOutbound();
                        LOGGER.debug("Show delta between file storage {} and {}", outboundEdge.getUnderlyingVertex().element().getId(), inboundEdge.getUnderlyingVertex().element().getId());
                    });
                }
            }

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
