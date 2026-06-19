package com.snjdigitalsolutions.lablensfx.graph;

import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphVertex;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.snjdigitalsolutions.lablensfx.orm.FileStorage;
import com.snjdigitalsolutions.lablensfx.orm.Relational;
import com.snjdigitalsolutions.lablensfx.state.SettingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class HierarchicalPlacementStrategy implements FileStorageHierarchicalStrategy, SmartPlacementStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalPlacementStrategy.class);
    private final SettingState settingState;

    public HierarchicalPlacementStrategy(SettingState settingState) {
        this.settingState = settingState;
    }

    @Override
    public <V extends Relational, E> void placeHierarchically(double width,
                                                              double height,
                                                              SmartGraphPanel<V, E> smartGraphPanel
    )
    {
        HashMap<Integer, SmartGraphVertex<V>> idOfGenericToVertexMap = new HashMap<>();
        HashMap<Integer, List<SmartGraphVertex<V>>> levelToSmartVertexMap = new HashMap<>();

        /*
         * Map all vertices to the ID of the underlying generic element
         */
        for (SmartGraphVertex<V> vertex : smartGraphPanel.getSmartVertices()) {
            idOfGenericToVertexMap.put(vertex.getUnderlyingVertex()
                                               .element()
                                               .getId()
                                               .intValue(), vertex);
        }

        /*
         * Get all level 0 vertices.
         * Level 0 vertex is one in which the parent value is 0
         */
        for (SmartGraphVertex<V> vertex : smartGraphPanel.getSmartVertices()) {
            if (vertex.getUnderlyingVertex()
                    .element()
                    .getParent() == 0) {
                if (!levelToSmartVertexMap.containsKey(0)) {
                    levelToSmartVertexMap.put(0, new ArrayList<>());
                }
                levelToSmartVertexMap.get(0)
                        .add(vertex);
            }
        }

        /*
         * Traverse all level 0 vertices to build the levels
         */
        for (SmartGraphVertex<V> vertex : levelToSmartVertexMap.get(0)) {
            V element = vertex.getUnderlyingVertex()
                    .element();
            if (element.getChild() != null && element.getChild() != 0) {
                traverse(element, 0, levelToSmartVertexMap, idOfGenericToVertexMap);
            }
        }

        LOGGER.debug("Levels collected");

        LOGGER.debug("Height: {}", height);
        LOGGER.debug("Width: {}", width);

        // Get number of levels
        int numberOfLevels = levelToSmartVertexMap.size();
        LOGGER.debug("Number of levels collected: {}", numberOfLevels);

        // Get maximum width
        int maxWidth = 0;
        for (List<SmartGraphVertex<V>> level : levelToSmartVertexMap.values()) {
            if (level.size() > maxWidth) {
                maxWidth = level.size();
            }
        }
        LOGGER.debug("Maximum width of smartgraph levels collected: {}", maxWidth);

        // Find level 0 placement
        int center = (Long.valueOf(Math.round(height / 2))).intValue();
        LOGGER.debug("Center of graph: {}", center);

        int levelZeroLocation = 0;
        int levelSpacing = settingState.getGraphLevelSpacing();
        if (((numberOfLevels) % 2) == 0) {
            //Even levels
            LOGGER.debug("Even levels");
            int firstSpacing = Double.valueOf((double) levelSpacing / 2)
                    .intValue();
            LOGGER.debug("First spacing: {}", firstSpacing);
            boolean firstCalculation = true;
            for (int i = 0; i < (numberOfLevels / 2); i++) {
                if (firstCalculation) {
                    levelZeroLocation = center - firstSpacing;
                    firstCalculation = false;
                } else {
                    levelZeroLocation = levelZeroLocation - levelSpacing;
                }
                LOGGER.debug("Level zero location: {}", levelZeroLocation);
            }
        } else {
            //Odd levels
            LOGGER.debug("Odd levels");
            int loopCount = Double.valueOf(Math.floor((double) numberOfLevels / 2))
                    .intValue();
            for (int i = 0; i < loopCount; i++) {
                levelZeroLocation = center - levelSpacing;
                LOGGER.debug("Level zero location: {}", levelZeroLocation);
            }
        }

        //Get graph horizontal placements
        int horizontalCenter = Double.valueOf(width / 2)
                .intValue();

        //Iterate levels and place
        for (int i = 0; i < numberOfLevels; i++) {
            for (SmartGraphVertex<V> vertex : levelToSmartVertexMap.get(i)) {
                if (i == 0) {
                    vertex.setPosition(horizontalCenter, levelZeroLocation);
                } else {
                    vertex.setPosition(horizontalCenter, levelZeroLocation + (levelSpacing * i));
                }
            }
        }
    }

    private <V extends Relational> void traverse(V element,
                                                 int callingLevel,
                                                 HashMap<Integer, List<SmartGraphVertex<V>>> levelToVertexMap,
                                                 HashMap<Integer, SmartGraphVertex<V>> idOfGenericToVertexMap
    )
    {
        callingLevel++;
        if (!levelToVertexMap.containsKey(callingLevel)) {
            levelToVertexMap.put(callingLevel, new ArrayList<>());
        }

        SmartGraphVertex<V> smartVertex = idOfGenericToVertexMap.get(element.getChild());
        V childElement = smartVertex.getUnderlyingVertex()
                .element();

        levelToVertexMap.get(callingLevel)
                .add(smartVertex);
        if (childElement.getChild() != null && childElement.getChild() != 0) {
            traverse(childElement, callingLevel, levelToVertexMap, idOfGenericToVertexMap);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V, E> void place(double width,
                             double height,
                             SmartGraphPanel<V, E> smartGraphPanel
    )
    {
        placeHierarchically(width, height, (SmartGraphPanel<FileStorage, E>) smartGraphPanel);
    }
}
