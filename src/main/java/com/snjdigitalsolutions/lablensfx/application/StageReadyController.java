package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.lablensfx.nodes.HostPane;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.springframework.stereotype.Component;

@Component
public class StageReadyController {

    @FXML
    private BorderPane rootPane;

    private final HostPane hostPane;

    public StageReadyController(HostPane hostPane) {
        this.hostPane = hostPane;
    }

    public void addHostPane(){
        rootPane.setLeft(hostPane);
    }

}
