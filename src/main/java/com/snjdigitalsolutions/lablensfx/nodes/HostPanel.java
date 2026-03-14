package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.properties.StatusBarProperties;
import com.snjdigitalsolutions.springbootutilityfx.event.StageReadyEvent;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import jakarta.annotation.PostConstruct;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class HostPanel extends GridPane implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostPanel.class);

    @FXML
    private Label hostNameLabel;
    @FXML
    private Label ipAddressLabel;
    @FXML
    private Label osLabel;
    @FXML
    private Label descriptionLabel;

    private final StatusBarProperties statusBarProperties;

    public HostPanel(@Value("classpath:/fxml/HostPanel.fxml") Resource fxml, StatusBarProperties statusBarProperties){
        this.statusBarProperties = statusBarProperties;
        NodeLoader.load(fxml, this);
    }

    @PostConstruct
    @Override
    public void performIntialization() {
        addSelectedStyle(this);
        this.getChildren().forEach(node -> {
            if (node instanceof Label){
                addSelectedStyle(node);
            }
        });
    }

    private void addSelectedStyle(Node node) {
        node.setOnMouseClicked(event -> {
            if (node.getStyleClass().contains("host-panel-selected")) {
                node.getStyleClass().remove("host-panel-selected");
                int currentValue = statusBarProperties.numberOfSelectedHostsProperty().getValue();
                currentValue--;
                LOGGER.debug("Panel deselected - {}", currentValue);
                statusBarProperties.numberOfSelectedHostsProperty().set(currentValue);
            } else {
                node.getStyleClass().add("host-panel-selected");
                int currentValue = statusBarProperties.numberOfSelectedHostsProperty().getValue();
                currentValue++;
                LOGGER.debug("Panel selected - {}", currentValue);
                statusBarProperties.numberOfSelectedHostsProperty().set(currentValue);
            }
        });

    }

    public Label getHostNameLabel() {
        return hostNameLabel;
    }

    public Label getIpAddressLabel() {
        return ipAddressLabel;
    }

    public Label getOsLabel() {
        return osLabel;
    }

    public Label getDescriptionLabel() {
        return descriptionLabel;
    }


}
