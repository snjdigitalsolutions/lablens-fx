package com.snjdigitalsolutions.lablensfx.nodes;

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graphview.*;
import com.snjdigitalsolutions.lablensfx.application.ChangeListenerRegistry;
import com.snjdigitalsolutions.lablensfx.graph.ChangedConfigurationGraphBase;
import com.snjdigitalsolutions.lablensfx.graph.ChangedConfigurationGraphBaseCreator;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.FileStorage;
import com.snjdigitalsolutions.lablensfx.orm.model.ComputeResourceModel;
import com.snjdigitalsolutions.lablensfx.repository.FileStorageRepository;
import com.snjdigitalsolutions.lablensfx.service.node.StatusBarService;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.state.ConfigurationCheckState;
import com.snjdigitalsolutions.lablensfx.state.ShowIpAddressState;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.sshd.common.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class DashboardPane extends AnchorPane implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardPane.class);
    private final FileStorageRepository fileStorageRepository;
    @FXML
    private HBox summaryPanelHBox;
    @FXML
    private TilePane hostFlowPane;
    @FXML
    private Label allOnLabel;
    @FXML
    private Label allOffLabel;
    @FXML
    private Label nextSnapshotLabel;

    private BooleanProperty performRefresh = new SimpleBooleanProperty(false);

    private final ObjectProvider<SummaryPanel> summaryPanelProvider;
    private final ObjectProvider<HostPanelLarge> hostPanelLargeProvider;
    private final ComputeResourceState computeResourceState;
    private final ShowIpAddressState showIpAddressState;
    private final StatusBarService statusBarService;
    private final ConfigurationCheckState configurationCheckState;
    private final ChangeListenerRegistry changeListenerRegistry;
    private final ChangedConfigurationGraphBaseCreator changedConfigurationGraphBaseCreator;

    /**
     * Creates the dashboard pane with the resources and services required to render summary panels.
     */
    public DashboardPane(@Value("classpath:/fxml/DashboardPane.fxml") Resource fxml,
                         ObjectProvider<SummaryPanel> summaryPanelProvider,
                         ObjectProvider<HostPanelLarge> hostPanelLargeProvider,
                         ComputeResourceState computeResourceState,
                         ShowIpAddressState showIpAddressState, StatusBarService statusBarService,
                         ConfigurationCheckState configurationCheckState,
                         ChangeListenerRegistry changeListenerRegistry,
                         ChangedConfigurationGraphBaseCreator changedConfigurationGraphBaseCreator,
                         FileStorageRepository fileStorageRepository
    )
    {
        this.summaryPanelProvider = summaryPanelProvider;
        this.hostPanelLargeProvider = hostPanelLargeProvider;
        this.computeResourceState = computeResourceState;
        this.showIpAddressState = showIpAddressState;
        this.statusBarService = statusBarService;
        this.configurationCheckState = configurationCheckState;
        this.changeListenerRegistry = changeListenerRegistry;
        this.changedConfigurationGraphBaseCreator = changedConfigurationGraphBaseCreator;
        NodeLoader.load(fxml, this);
        this.fileStorageRepository = fileStorageRepository;
    }

    /**
     * Initializes all summary panels and wires them to their state properties.
     */
    @Override
    public void performIntialization() {
        performRefresh.bind(computeResourceState.computeResourcesLoadedProperty());
        computeResourceState.computeResourcesLoadedProperty()
                .addListener((obj, oldVal, newVal) -> {
                    if (newVal) {
                        refresh();
                    }
                });
        summaryPanelHBox.getChildren()
                .add(createSummaryPanel(SummaryPanelType.NUM_HOSTS));
        summaryPanelHBox.getChildren()
                .add(createSummaryPanel(SummaryPanelType.NUM_ONLINE));
        summaryPanelHBox.getChildren()
                .add(createSummaryPanel(SummaryPanelType.NUM_LOG_ERRORS));
        summaryPanelHBox.getChildren()
                .add(createSummaryPanel(SummaryPanelType.NUM_CONFIG_CHANGE));
        computeResourceState.getComputeResourcesMap()
                .addListener((MapChangeListener<Long, ComputeResource>) change -> {
                    if (change.wasAdded() && !change.wasRemoved()) {
                        if (performRefresh.getValue()) {
                            refresh();
                        }
                    } else if (!change.wasAdded() && change.wasRemoved()) {
                        if (performRefresh.getValue()) {
                            refresh();
                        }
                    }
                });
        this.widthProperty()
                .addListener((obj, oldVal, newVal) -> {
                    hostFlowPane.setMaxWidth(newVal.doubleValue());
                });
        showIpAddressState.showIpPropertyProperty()
                .addListener((obj, oldVal, newVal) -> {
                    refresh();
                });
        allOnLabel.getStyleClass().add("hyper-click-blue");
        allOnLabel.setOnMouseClicked(event -> {
            computeResourceState.getComputeResourceHostPanelLargeMap().values().forEach(hostPanelLarge -> {
                hostPanelLarge.changeToggleState(true);
            });
        });
        allOffLabel.getStyleClass().add("hyper-click-blue");
        allOffLabel.setOnMouseClicked(event -> {
            computeResourceState.getComputeResourceHostPanelLargeMap().values().forEach(hostPanelLarge -> {
                hostPanelLarge.changeToggleState(false);
            });
        });
        nextSnapshotLabel.setText(configurationCheckState.getCheckStatus());
        ChangeListener<String> nextSnapshotLabelChangeListener = (obj, oldVal, newVal) -> {
            if (newVal != null) {
                nextSnapshotLabel.setText(newVal);
            } else {
                nextSnapshotLabel.setText("");
            }
        };
        changeListenerRegistry.add(this, configurationCheckState.checkStatusProperty(), nextSnapshotLabelChangeListener);
    }

    /**
     * Constructs a summary panel of the given type.
     *
     * @param type the type of summary panel to create
     * @return the configured {@link SummaryPanel}
     */
    private SummaryPanel createSummaryPanel(SummaryPanelType type) {
        SummaryPanel panel = summaryPanelProvider.getObject();
        panel.performInitialization();
        panel.setHeaderLabelText(type.getHeader());
        panel.setMoreInfoLabel(type.getMoreInfo());
        if (!type.getCssClass()
                .isEmpty()) {
            panel.setCountLabelStyleClass(type.getCssClass());
        }
        HBox.setHgrow(panel, Priority.ALWAYS);
        addListenerForLabel(panel, type);
        if (type == SummaryPanelType.NUM_CONFIG_CHANGE) {
            Consumer<Event> eventConsumer = event -> {
                if (event.getSource() instanceof Label) {
                    String labelText = ((Label)event.getSource()).getText();
                    if (NumberUtils.isIntegerNumber(labelText) && Integer.parseInt(labelText) > 0) {
                        LOGGER.info("Number of changed configuration files: {}", Integer.parseInt(labelText));
                        List<ChangedConfigurationGraphBase> changedConfigurations = changedConfigurationGraphBaseCreator.createChangedConfigurationGraphBase();
                        for (ChangedConfigurationGraphBase changedConfigurationGraphBase : changedConfigurations) {
                            Digraph<FileStorage, String> graph = changedConfigurationGraphBase.getChangeGraph();


                            // using for PoC only
                            SmartPlacementStrategy initialPlacement = new SmartCircularSortedPlacementStrategy();
                            ForceDirectedLayoutStrategy<FileStorage> automaticPlacementStrategy = new ForceDirectedSpringGravityLayoutStrategy<>();
                            SmartGraphPanel<FileStorage,String> graphView = new SmartGraphPanel<>(graph, initialPlacement, automaticPlacementStrategy);
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
            };
            panel.setLabelListener(eventConsumer);
            LOGGER.debug("Added consumer to configuration change count label");
        }
        return panel;
    }

    /**
     * Attaches a property change listener so the panel's count label updates when state changes.
     *
     * @param panel the target summary panel
     * @param type  the panel type that determines which property to observe
     */
    private void addListenerForLabel(SummaryPanel panel,
                                     SummaryPanelType type
    )
    {
        switch (type) {
            case NUM_HOSTS -> {
                computeResourceState.computeResourcesMapProperty()
                        .addListener((MapChangeListener<Long, ComputeResource>) change -> {
                            panel.setCountLabel(Integer.toString(computeResourceState.getComputeResourcesMap()
                                                                         .size()));
                        });
            }
            case NUM_ONLINE -> {
                computeResourceState.hostsOnlineCountProperty()
                        .addListener((obj, oldVal, newVal) -> {
                            panel.setCountLabel(newVal.toString());
                        });
            }
            case NUM_CONFIG_CHANGE -> {
                computeResourceState.configurationChangeCountProperty()
                        .addListener((obj, oldVal, newVal) -> {
                            panel.setCountLabel(newVal.toString());
                        });
            }
        }
    }

    /**
     * Refreshes all summary panel counts from the current state.
     */
    public void refresh() {
        LOGGER.debug("Refreshing dashboard");
        clearHostPanel();
        Map<String, HostPanelLarge> ipAddressToPanelMap = new HashMap<>();
        computeResourceState.getComputeResourcesMap()
                .values()
                .forEach(resource -> {
                    HostPanelLarge panel = hostPanelLargeProvider.getObject();
                    panel.performInitialization(resource.getId());
                    panel.setResourceModel(new ComputeResourceModel(resource));
                    panel.addToggleListener();
                    panel.getStyleClass()
                            .add("host-panel");
                    if (computeResourceState.getComputeResourceOnlineStatusMap()
                            .containsKey(resource.getId())) {
                        panel.getStatusIndicator()
                                .hostSshStatusProperty()
                                .setValue(computeResourceState.getComputeResourceOnlineStatusMap()
                                                  .get(resource.getId()));
                    }
                    computeResourceState.getComputeResourceHostPanelLargeMap()
                            .put(resource.getId(), panel);
                    ipAddressToPanelMap.put(resource.getIpAddress(), panel);

                });

        List<String> ipAddresses = new ArrayList<>(ipAddressToPanelMap.keySet());
        ipAddresses.sort(String::compareTo);
        ipAddresses.forEach(ip -> {
            hostFlowPane.getChildren()
                    .add(ipAddressToPanelMap.get(ip));
        });
    }

    /**
     * Removes all host panels from the host-panel container.
     */
    private void clearHostPanel() {
        hostFlowPane.getChildren()
                .clear();
        statusBarService.setSelectedHostCount(0);
    }
}
