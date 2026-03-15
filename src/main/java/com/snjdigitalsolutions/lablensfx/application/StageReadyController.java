package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.lablensfx.nodes.DashboardPane;
import com.snjdigitalsolutions.lablensfx.nodes.HostFormPane;
import com.snjdigitalsolutions.lablensfx.nodes.HostPane;
import com.snjdigitalsolutions.lablensfx.properties.StatusBarProperties;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StageReadyController implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(StageReadyController.class);

    @FXML
    private StatusBar statusBar;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Button addHostButton;
    @FXML
    private MenuItem deleteSelectedHostsMenuItem;

    private final HostPane hostPane;
    private final HostFormPane hostFormPane;
    private final StatusBarProperties statusBarProperties;
    private final DashboardPane dashboardPane;

    public StageReadyController(HostPane hostPane, HostFormPane hostFormPane, StatusBarProperties statusBarProperties, DashboardPane dashboardPane) {
        this.hostPane = hostPane;
        this.hostFormPane = hostFormPane;
        this.statusBarProperties = statusBarProperties;
        this.dashboardPane = dashboardPane;
    }

    @Override
    public void performIntialization() {
        rootPane.setLeft(hostPane);
        rootPane.setCenter(dashboardPane);
        statusBar.textProperty().bind(statusBarProperties.statusProperty());
        deleteSelectedHostsMenuItem.disableProperty().bind(statusBarProperties.disableDeleteHostMenuItemProperty());
        addHostButton.setOnAction(buttonEvent -> {
            hostFormPane.showFormPane();
        });
    }

}
