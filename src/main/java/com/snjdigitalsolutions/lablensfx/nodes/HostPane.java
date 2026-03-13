package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class HostPane extends AnchorPane implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostPane.class);

    @FXML
    private VBox panelVBox;

    private final ComputeResourceRepository computeResourceRepository;
    private final ObjectProvider<HostPanel> hostPanelProvider;
    private final HostFormPane hostFormPane;

    public HostPane(@Value("classpath:/fxml/HostPane.fxml") Resource fxml,
                    ComputeResourceRepository computeResourceRepository,
                    ObjectProvider<HostPanel> hostPanelProvider,
                    HostFormPane hostFormPane) {
        this.computeResourceRepository = computeResourceRepository;
        this.hostPanelProvider = hostPanelProvider;
        this.hostFormPane = hostFormPane;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        panelVBox.setAlignment(Pos.CENTER_LEFT);
        hostFormPane.setOnSubmit(this::refresh);
        refresh();
    }

    private void refresh() {
        LOGGER.debug("Refreshing host panels");
        panelVBox.getChildren().clear();
        computeResourceRepository.findAll().forEach(resource -> {
            LOGGER.debug("Adding panel for resource: {}", resource.getHostName());
            HostPanel panel = hostPanelProvider.getObject();
            panel.getStyleClass().add("host-panel");
            panel.getHostNameLabel().setText(resource.getHostName());
            panel.getIpAddressLabel().setText(resource.getIpAddress());
            panel.getOsLabel().setText(resource.getOperatingSystem());
            panel.getDescriptionLabel().setText(resource.getDescription());
            panel.maxWidthProperty().bind(panelVBox.widthProperty());
            panel.getDescriptionLabel().setWrapText(true);
            panelVBox.getChildren().add(panel);
        });
    }
}
