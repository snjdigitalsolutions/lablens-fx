package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.properties.GlobalProperties;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.springbootutilityfx.event.StageReadyEvent;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.IpAddressUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import jakarta.annotation.PostConstruct;
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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HostPane extends AnchorPane implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostPane.class);

    @FXML
    private VBox panelVBox;

    private final ComputeResourceRepository computeResourceRepository;
    private final ObjectProvider<HostPanel> hostPanelProvider;
    private final HostFormPane hostFormPane;
    private final GlobalProperties globalProperties;
    private final IpAddressUtility ipAddressUtility;

    public HostPane(@Value("classpath:/fxml/HostPane.fxml") Resource fxml,
                    ComputeResourceRepository computeResourceRepository,
                    ObjectProvider<HostPanel> hostPanelProvider,
                    HostFormPane hostFormPane, GlobalProperties globalProperties, IpAddressUtility ipAddressUtility) {
        this.computeResourceRepository = computeResourceRepository;
        this.hostPanelProvider = hostPanelProvider;
        this.hostFormPane = hostFormPane;
        this.globalProperties = globalProperties;
        this.ipAddressUtility = ipAddressUtility;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        panelVBox.setAlignment(Pos.CENTER_LEFT);
        hostFormPane.setOnSubmit(this::refresh);
        refresh();
    }

    public void refresh() {
        LOGGER.debug("Refreshing host panels");
        panelVBox.getChildren().clear();
        Map<String,HostPanel> ipAddressToHostPanelMap = new HashMap<>();
        computeResourceRepository.findAll().forEach(resource -> {
            LOGGER.debug("Adding panel for resource: {}", resource.getHostName());
            HostPanel panel = hostPanelProvider.getObject();
            panel.getStyleClass().add("host-panel");
            panel.getHostNameLabel().setText(resource.getHostName());
            panel.getIpAddressLabel().setText(resource.getIpAddress());
            panel.maxWidthProperty().bind(panelVBox.widthProperty());
            panel.setHostId(resource.getId());
            ipAddressToHostPanelMap.put(panel.getIpAddressLabel().getText(), panel);
        });

        List<String> sortedIps = ipAddressUtility.sortIpAddresses(new ArrayList<>(ipAddressToHostPanelMap.keySet()));
        sortedIps.forEach(address -> {
            panelVBox.getChildren().add(ipAddressToHostPanelMap.get(address));
        });

        globalProperties.getNumberOfHostsProperty().setValue(panelVBox.getChildren().size());
    }

    public void removeHostPanel(HostPanel hostPanel) {
        panelVBox.getChildren().remove(hostPanel);
        computeResourceRepository.deleteById(hostPanel.getHostId());
    }
}
