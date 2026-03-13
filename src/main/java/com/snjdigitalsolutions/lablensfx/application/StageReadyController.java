package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.lablensfx.nodes.HostFormPane;
import com.snjdigitalsolutions.lablensfx.nodes.HostPane;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class StageReadyController implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(StageReadyController.class);

    @FXML
    private BorderPane rootPane;
    @FXML
    private Button addHostButton;

    private final HostPane hostPane;
    private final HostFormPane hostFormPane;

    public StageReadyController(HostPane hostPane, HostFormPane hostFormPane) {
        this.hostPane = hostPane;
        this.hostFormPane = hostFormPane;
    }

    public void addHostPane(){
        rootPane.setLeft(hostPane);
    }

    @Override
    public void performIntialization() {
        LOGGER.debug("Initializing...");
        hostPane.performIntialization();
        hostFormPane.performIntialization();
        addHostButton.setOnAction(event -> {
            hostFormPane.showFormPane();
        });
    }
}
