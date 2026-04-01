package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationPane extends AnchorPane implements SpringInitializableNode {

    @FXML
    private SplitPane splitPane;

    private double splitPaneDividerPosition = 0.5;

    public ConfigurationPane(@Value("classpath:/fxml/ConfigurationPane.fxml") Resource fxml) {
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        splitPane.getDividers().get(0).positionProperty().addListener((obj, oldVal, newVal) -> {
            if (newVal.doubleValue() != splitPaneDividerPosition){
                splitPane.setDividerPosition(0, splitPaneDividerPosition);
            }
        });
    }

}
