package com.snjdigitalsolutions.lablensfx.application;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.snjdigitalsolutions.lablensfx.nodes.*;
import com.snjdigitalsolutions.lablensfx.orm.Setting;
import com.snjdigitalsolutions.lablensfx.repository.SettingRepository;
import com.snjdigitalsolutions.lablensfx.service.FilePersistenceService;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.service.PassPhraseMode;
import com.snjdigitalsolutions.lablensfx.service.VerifyHostConfigurationService;
import com.snjdigitalsolutions.lablensfx.service.node.ConfigurationPaneService;
import com.snjdigitalsolutions.lablensfx.service.node.StatusBarService;
import com.snjdigitalsolutions.lablensfx.setting.Interval;
import com.snjdigitalsolutions.lablensfx.setting.SettingType;
import com.snjdigitalsolutions.lablensfx.shapes.SshPassphraseIndicator;
import com.snjdigitalsolutions.lablensfx.state.*;
import com.snjdigitalsolutions.lablensfx.task.ConfigurationChangeCheckTask;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.StageNodeBuilder;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.TooltipGenerator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class LabLensFxBootReadyController implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabLensFxBootReadyController.class);

    @Value("${application.testbutton.enabled}")
    private Boolean testButtonEnabled;

    @FXML
    private StackPane stackPane;
    @FXML
    private BorderPane borderPane;
    @FXML
    private StatusBar statusBar;

    @FXML
    private Button addHostButton;
    @FXML
    private Button sshButton;
    @FXML
    private Button configButton;
    @FXML
    private Button logButton;
    @FXML
    private Button timelineButton;
    @FXML
    private Button dashboardButton;
    @FXML
    private Button settingsButton;

    @FXML
    private MenuItem deleteSelectedHostsMenuItem;
    @FXML
    private MenuItem showHideIpMenuItem;
    @FXML
    private MenuItem confirmConfigurationSelectionChangesMenuItem;
    @FXML
    private MenuItem verifyPathPrivilegeMenuItem;
    @FXML
    private MenuItem checkForConfigurationChangesMenuItem;
    @FXML
    private MenuItem testFunctionMenuItem;
    @FXML
    private FontAwesomeIconView showHideIpIconView;
    @FXML
    private FontAwesomeIconView confirmChangeIconView;

    private final ObjectProvider<SshPassphraseIndicator> statusIndicatorProvider;
    private final HostPane hostPane;
    private final HostFormPane hostFormPane;
    private final StatusBarState statusBarState;
    private final SshState sshState;
    private final DashboardPane dashboardPane;
    private final ConfigurationPane configurationPane;
    private final ConfigurationPaneService configurationPaneService;
    private final HostManagementService hostManagementService;
    private final PassphraseDialog passphraseDialog;
    private final ShowIpAddressState showIpAddressState;
    private final VerifyHostConfigurationService verifyHostConfigurationService;
    private final SelectedViewState selectedViewState;
    private final MenuItemSelectionState menuItemSelectionState;
    private final SettingState settingState;
    private final SettingRepository settingRepository;
    private final AlertUtility alertUtility;
    private final StatusBarService statusBarService;
    private final ApplicationState applicationState;
    private final ChangeListenerRegistry changeListenerRegistry;
    private final LoadingOverlay loadingOverlay;
    private final TooltipGenerator tooltipGenerator;
    private final SettingsDialogPane settingsDialogPane;

    private SshPassphraseIndicator indicator;

    private ToggleButton dashboardToggleButton;
    private ToggleButton configToggleButton;
    private ToggleButton logToggleButton;
    private ToggleButton timelineToggleButton;

    private final ObjectProvider<ConfigurationChangeCheckTask> configurationChangeCheckTaskObjectProvider;

    /**
     * Creates the boot-ready controller with all required UI and service dependencies.
     */
    public LabLensFxBootReadyController(ObjectProvider<SshPassphraseIndicator> statusIndicatorProvider,
                                        HostPane hostPane,
                                        HostFormPane hostFormPane,
                                        StatusBarState statusBarState,
                                        SshState sshState,
                                        DashboardPane dashboardPane,
                                        HostManagementService hostManagementService,
                                        TooltipGenerator tooltipGenerator,
                                        ConfigurationPane configurationPane,
                                        ConfigurationPaneService configurationPaneService,
                                        PassphraseDialog passphraseDialog,
                                        ShowIpAddressState showIpAddressState,
                                        VerifyHostConfigurationService verifyHostConfigurationService,
                                        SelectedViewState selectedViewState,
                                        MenuItemSelectionState menuItemSelectionState,
                                        SettingState settingState,
                                        SettingRepository settingRepository,
                                        AlertUtility alertUtility, StatusBarService statusBarService,
                                        ApplicationState applicationState,
                                        ChangeListenerRegistry changeListenerRegistry,
                                        LoadingOverlay loadingOverlay,
                                        SettingsDialogPane settingsDialogPane,
                                        ObjectProvider<ConfigurationChangeCheckTask> configurationChangeCheckTaskObjectProvider
    )
    {
        this.statusIndicatorProvider = statusIndicatorProvider;
        this.hostPane = hostPane;
        this.hostFormPane = hostFormPane;
        this.statusBarState = statusBarState;
        this.sshState = sshState;
        this.dashboardPane = dashboardPane;
        this.hostManagementService = hostManagementService;
        this.tooltipGenerator = tooltipGenerator;
        this.configurationPane = configurationPane;
        this.configurationPaneService = configurationPaneService;
        this.passphraseDialog = passphraseDialog;
        this.showIpAddressState = showIpAddressState;
        this.verifyHostConfigurationService = verifyHostConfigurationService;
        this.selectedViewState = selectedViewState;
        this.menuItemSelectionState = menuItemSelectionState;
        this.settingState = settingState;
        this.settingRepository = settingRepository;
        this.alertUtility = alertUtility;
        this.statusBarService = statusBarService;
        this.applicationState = applicationState;
        this.changeListenerRegistry = changeListenerRegistry;
        this.loadingOverlay = loadingOverlay;
        this.settingsDialogPane = settingsDialogPane;
        this.configurationChangeCheckTaskObjectProvider = configurationChangeCheckTaskObjectProvider;
    }

    /**
     * Orchestrates full UI initialization after the Spring context is ready.
     */
    @Override
    public void performIntialization() {
        borderPane.setLeft(hostPane);

        dashboardToggleButton = new ToggleButton("Dashboard");
        dashboardToggleButton.setFocusTraversable(false);
        configToggleButton = new ToggleButton("Configurations");
        configToggleButton.setFocusTraversable(false);
        logToggleButton = new ToggleButton("Logs");
        logToggleButton.setFocusTraversable(false);
        timelineToggleButton = new ToggleButton("Timeline");
        timelineToggleButton.setFocusTraversable(false);

        initializeLoadingOverlay();
        setDashboardVisible();
        initializeStatusBar();
        initializeViewButtons();
        initializeSshCredentialIndicator();
        initializeDeleteSelectedHostMenuItem();
        initializeAddHostButton();
        initializeSshButton();
        initializePrivilegeMenuItem();
        initializeApplicationSettings();
        initializeConfigurationButton();
        initializeDashboardButton();
        sshState.passPhraseModeProperty()
                .addListener((obj, oldVal, newVal) -> {
                    indicator.passPhraseMode()
                            .setValue(newVal);
                });
        HBox buttonBox = (HBox)sshButton.getParent();
        buttonBox.getChildren().remove(configButton);
        buttonBox.getChildren().remove(dashboardButton);
        buttonBox.getChildren().remove(logButton);
        buttonBox.getChildren().remove(timelineButton);
        configButton.visibleProperty().setValue(false);
        dashboardButton.visibleProperty().setValue(false);
        logButton.visibleProperty().setValue(false);
        timelineButton.visibleProperty().setValue(false);
        SegmentedButton segmentedButton = new SegmentedButton();
        segmentedButton.getButtons().addAll(dashboardToggleButton, configToggleButton, logToggleButton, timelineToggleButton);
        // Apply the listener to each button
        dashboardToggleButton.setSelected(true);
        ToggleGroup group = segmentedButton.getToggleGroup();
        group.getToggles().addAll(dashboardToggleButton, configToggleButton, logToggleButton, timelineToggleButton);
        for (Toggle toggle : group.getToggles()) {
            toggle.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (!isNowSelected && group.getSelectedToggle() == null) {
                    LOGGER.debug(("Toggle selection changed"));
                    toggle.setSelected(true);
                }
            });
        }
        buttonBox.getChildren().add(1, segmentedButton);
        if (!testButtonEnabled){
            testFunctionMenuItem.setVisible(false);
        }

        checkForConfigurationChangesMenuItem.setOnAction(event -> {
            Thread.ofVirtual().start(configurationChangeCheckTaskObjectProvider.getObject());
        });

        testFunctionMenuItem.setOnAction(event -> {

        });

        settingsButton.setOnAction(event -> {
            StageNodeBuilder.builder()
                    .setModality(Modality.APPLICATION_MODAL)
                    .setResizable(false)
                    .setTitle("Settings")
                    .setNode(settingsDialogPane)
                    .buildAndShow();
        });
    }

    /**
     * Configures and installs the loading overlay onto the root pane.
     */
    private void initializeLoadingOverlay() {
        stackPane.getChildren().add(loadingOverlay);
        ChangeListener<Boolean> loadingListener = (obj, oldVal, newVal) -> {
            if (newVal) {
                BoxBlur blur = new BoxBlur(5,5,3);
                borderPane.setEffect(blur);
            } else {
                borderPane.setEffect(null);
            }
        };
        changeListenerRegistry.add(this, applicationState.loadingDataProperty(), loadingListener);
    }

    /**
     * Binds the status bar component to its backing state properties.
     */
    private void initializeStatusBar() {
        statusBarService.setStatusbar(statusBar);
    }

    /**
     * Wires the dashboard navigation button to show the dashboard view.
     */
    private void initializeDashboardButton() {
        dashboardToggleButton.setOnAction(event -> {
           setDashboardVisible();
        });
        dashboardButton.setOnAction(event -> {
            setDashboardVisible();
        });
    }

    /**
     * Wires the configuration navigation button to show the configuration view.
     */
    private void initializeConfigurationButton() {
        configToggleButton.setOnAction(event -> {
            LOGGER.debug("Configuration button clicked");
            setConfigurationVisible();
        });
        configButton.setOnAction(event -> {
            LOGGER.debug("Configuration button clicked");
            setConfigurationVisible();
        });
    }

    /**
     * Loads persisted application settings from the repository into state.
     */
    private void initializeApplicationSettings() {
        //Ensure default values are populated in datasource
        for (SettingType type : SettingType.values()) {
            Optional<Setting> settingValue = settingRepository.findBySettingName(type.getName());
            if (settingValue.isEmpty()) {
                Setting setting = new Setting();
                setting.setSettingName(type.getName());
                if (type.isBoolType()) {
                    setting.setBoolValue((boolean) type.getDefaultValue());
                } else {
                    if (type.getDefaultValue() instanceof Interval){
                        setting.setStringValue(((Interval)type.getDefaultValue()).displayValue());
                    } else {
                        setting.setStringValue((String) type.getDefaultValue());
                    }
                }
                settingRepository.save(setting);
                LOGGER.debug("Setting loaded: {} with default value of {}", type.getName(), type.getDefaultValue());
            } else {
                LOGGER.debug("Setting in database: {}", type.getName());
            }
        }

        //Load all settings
        settingRepository.findAll()
                .forEach(setting -> {
                    Arrays.stream(SettingType.values())
                            .filter(settingType -> settingType.getName()
                                    .equals(setting.getSettingName()))
                            .forEach(match -> {
                                switch (match) {
                                    case CONFIG_CONFIRMATION -> {
                                        settingState.promptWhenConfigSelectionChangesProperty()
                                                .setValue(setting.getBoolValue());
                                    }
                                    case SHOW_IP_ADDRESSES -> {
                                        settingState.showIPsProperty()
                                                .setValue(setting.getBoolValue());
                                    }
                                }
                            });
                });

        //Setup change listeners
        settingState.showIPsProperty()
                .addListener((obj, oldVal, newVal) -> {
                    LOGGER.debug("Show IP property value: {}", newVal);
                    Optional<Setting> optSetting = settingRepository.findBySettingName(SettingType.SHOW_IP_ADDRESSES.getName());
                    optSetting.ifPresent(setting -> setting.setBoolValue(newVal));
                    settingRepository.save(optSetting.get());
                });

        settingState.promptWhenConfigSelectionChangesProperty()
                .addListener((obj, oldVal, newVal) -> {
                    Optional<Setting> optSetting = settingRepository.findBySettingName(SettingType.CONFIG_CONFIRMATION.getName());
                    optSetting.ifPresent(value -> value.setBoolValue(newVal));
                    settingRepository.save(optSetting.get());
                });

        settingState.settingsLoadedProperty()
                .addListener((obj, oldVal, newVal) -> {
                    LOGGER.debug("Settings loaded...");
                    initializeShowHideIpMenuItemAfterSettingsLoaded();
                    initializeConfirmConfigurationSelectionChangeAfterSettingsLoaded();
                });

        settingState.settingsLoadedProperty()
                .setValue(true);
    }

    /**
     * Configures the privilege-elevation menu item based on current SSH state.
     */
    private void initializePrivilegeMenuItem() {
        verifyPathPrivilegeMenuItem.setDisable(true);
        verifyPathPrivilegeMenuItem.setOnAction(event -> {
            verifyHostConfigurationService.startTask();
        });
        statusBarState.numberOfSelectedHostsProperty()
                .addListener((obj, oldVal, newVal) -> {
                    verifyPathPrivilegeMenuItem.setDisable(newVal.intValue() != 1);
                });
    }

    /**
     * Makes the configuration pane visible and hides the dashboard pane.
     */
    private void setConfigurationVisible() {
        selectedViewState.selectedViewProperty()
                .setValue(ApplicationView.CONFIGURATIONS);
        configurationPaneService.loadExistingPaths();
        borderPane.setCenter(configurationPane);
    }

    /**
     * Makes the dashboard pane visible and hides the configuration pane.
     */
    private void setDashboardVisible() {
        selectedViewState.selectedViewProperty()
                .setValue(ApplicationView.DASHBOARD);
        borderPane.setCenter(dashboardPane);
    }

    /**
     * Registers click handlers for all primary navigation view buttons.
     */
    private void initializeViewButtons() {
        disableNonDashboardButtons(true);
        statusBarState.numberOfSelectedHostsProperty()
                .addListener((obj, oldVal, newVal) -> {
                    disableNonDashboardButtons(newVal.intValue() != 1);
                });
    }

    /**
     * Enables or disables all non-dashboard toolbar buttons.
     *
     * @param value {@code true} to disable; {@code false} to enable
     */
    private void disableNonDashboardButtons(boolean value) {
        configButton.setDisable(value);
        configToggleButton.setDisable(value);
        addHostButton.setDisable(!value);
        logButton.setDisable(value);
        logToggleButton.setDisable(value);
        timelineButton.setDisable(value);
        timelineToggleButton.setDisable(value);
        if (value) {
            setDashboardVisible();
            dashboardToggleButton.setSelected(true);
        }
    }

    /**
     * Binds the SSH credential indicator shape to the current passphrase mode state.
     */
    private void initializeSshCredentialIndicator() {
        indicator = statusIndicatorProvider.getObject();
        indicator.passPhraseMode()
                .setValue(PassPhraseMode.NOT_PROVIDED);
        statusBar.getRightItems()
                .add(indicator);
    }

    /**
     * Wires the SSH button action to open the passphrase dialog.
     */
    private void initializeSshButton() {
        sshButton.setTooltip(tooltipGenerator.generateTooltip("Set SSH credentials and verify host connectivity."));
        sshButton.setOnAction(event -> {
            passphraseDialog.showDialog();
            passphraseDialog.setPostDialogAction(hostManagementService::loadComputeResources);
        });
    }

    /**
     * Wires the add-host button to display the host-form pane.
     */
    private void initializeAddHostButton() {
        addHostButton.setOnAction(buttonEvent -> {
            hostFormPane.showPane();
        });
    }

    /**
     * Binds the delete-selected-host menu item to the current selection state.
     */
    private void initializeDeleteSelectedHostMenuItem() {
        deleteSelectedHostsMenuItem.disableProperty()
                .bind(statusBarState.disableDeleteHostMenuItemProperty());
        deleteSelectedHostsMenuItem.setOnAction(event -> {
            alertUtility.confirmAlert("Delete Hosts", "Are you sure you want to delete selected hosts?", () -> {
                hostManagementService.deleteSelectedHosts();
            });
        });
    }

    /**
     * Adds a listener that shows or hides IP addresses after settings finish loading.
     */
    private void initializeShowHideIpMenuItemAfterSettingsLoaded() {
        if (settingState.isShowIPs()) {
            showHideIpIconView.setIcon(FontAwesomeIcon.CHECK);
            showIpAddressState.showIpPropertyProperty()
                    .setValue(true);
        } else {
            showHideIpIconView.setIcon(FontAwesomeIcon.TIMES);
            showIpAddressState.showIpPropertyProperty()
                    .setValue(false);
        }
        showHideIpMenuItem.setOnAction(event -> {
            if (settingState.isShowIPs()) {
                showHideIpIconView.setIcon(FontAwesomeIcon.TIMES);
                showIpAddressState.showIpPropertyProperty()
                        .setValue(false);
                settingState.showIPsProperty()
                        .setValue(false);
            } else {
                showHideIpIconView.setIcon(FontAwesomeIcon.CHECK);
                showIpAddressState.showIpPropertyProperty()
                        .setValue(true);
                settingState.showIPsProperty()
                        .setValue(true);
            }
        });
    }

    /**
     * Adds a listener that enables the confirm-on-config-change setting after load.
     */
    private void initializeConfirmConfigurationSelectionChangeAfterSettingsLoaded() {
        if (settingState.isPromptWhenConfigSelectionChanges()) {
            confirmChangeIconView.setIcon(FontAwesomeIcon.CHECK);
            menuItemSelectionState.confirmConfigurationChangeSelectionProperty()
                    .setValue(true);
        } else {
            confirmChangeIconView.setIcon(FontAwesomeIcon.TIMES);
            menuItemSelectionState.confirmConfigurationChangeSelectionProperty()
                    .setValue(false);
        }
        confirmConfigurationSelectionChangesMenuItem.setOnAction(event -> {
            if (menuItemSelectionState.isConfirmConfigurationChangeSelection()) {
                confirmChangeIconView.setIcon(FontAwesomeIcon.TIMES);
                menuItemSelectionState.confirmConfigurationChangeSelectionProperty()
                        .setValue(false);
                settingState.promptWhenConfigSelectionChangesProperty()
                        .setValue(false);
            } else {
                confirmChangeIconView.setIcon(FontAwesomeIcon.CHECK);
                menuItemSelectionState.confirmConfigurationChangeSelectionProperty()
                        .setValue(true);
                settingState.promptWhenConfigSelectionChangesProperty()
                        .setValue(true);
            }
        });
    }

}
