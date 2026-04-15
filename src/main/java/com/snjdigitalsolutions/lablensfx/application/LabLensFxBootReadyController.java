package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.lablensfx.nodes.*;
import com.snjdigitalsolutions.lablensfx.orm.Setting;
import com.snjdigitalsolutions.lablensfx.repository.SettingRepository;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.service.PassPhraseMode;
import com.snjdigitalsolutions.lablensfx.service.VerifyHostConfigurationService;
import com.snjdigitalsolutions.lablensfx.service.node.ConfigurationPaneService;
import com.snjdigitalsolutions.lablensfx.service.node.StatusBarService;
import com.snjdigitalsolutions.lablensfx.setting.SettingType;
import com.snjdigitalsolutions.lablensfx.shapes.SshPassphraseIndicator;
import com.snjdigitalsolutions.lablensfx.state.*;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.TooltipGenerator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class LabLensFxBootReadyController implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabLensFxBootReadyController.class);
    private final TooltipGenerator tooltipGenerator;

    @FXML
    private StatusBar statusBar;
    @FXML
    private BorderPane rootPane;

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
    private MenuItem deleteSelectedHostsMenuItem;
    @FXML
    private MenuItem showHideIpMenuItem;
    @FXML
    private MenuItem confirmConfigurationSelectionChangesMenuItem;
    @FXML
    private MenuItem verifyPathPrivilegeMenuItem;
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

    private SshPassphraseIndicator indicator;

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
                                        AlertUtility alertUtility, StatusBarService statusBarService
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
    }

    @Override
    public void performIntialization() {
        rootPane.setLeft(hostPane);
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
    }

    private void initializeStatusBar() {
        statusBarService.setStatusbar(statusBar);
    }

    private void initializeDashboardButton() {
        dashboardButton.setOnAction(event -> {
            setDashboardVisible();
        });
    }

    private void initializeConfigurationButton() {
        configButton.setOnAction(event -> {
            setConfigurationVisible();
        });
    }

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
                    setting.setStringValue((String) type.getDefaultValue());
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

    private void setConfigurationVisible() {
        selectedViewState.selectedViewProperty()
                .setValue(ApplicationView.CONFIGURATIONS);
        configurationPaneService.loadExistingPaths();
        rootPane.setCenter(configurationPane);
    }

    private void setDashboardVisible() {
        selectedViewState.selectedViewProperty()
                .setValue(ApplicationView.DASHBOARD);
        rootPane.setCenter(dashboardPane);
    }

    private void initializeViewButtons() {
        disableNonDashboardButtons(true);
        statusBarState.numberOfSelectedHostsProperty()
                .addListener((obj, oldVal, newVal) -> {
                    disableNonDashboardButtons(newVal.intValue() != 1);
                });
    }

    private void disableNonDashboardButtons(boolean value) {
        configButton.setDisable(value);
        addHostButton.setDisable(!value);
        logButton.setDisable(value);
        timelineButton.setDisable(value);
        if (value) {
            setDashboardVisible();
        }
    }

    private void initializeSshCredentialIndicator() {
        indicator = statusIndicatorProvider.getObject();
        indicator.passPhraseMode()
                .setValue(PassPhraseMode.NOT_PROVIDED);
        statusBar.getRightItems()
                .add(indicator);
    }

    private void initializeSshButton() {
        sshButton.setTooltip(tooltipGenerator.generateTooltip("Set SSH credentials and verify host connectivity."));
        sshButton.setOnAction(event -> {
            passphraseDialog.showDialog();
            passphraseDialog.setPostDialogAction(hostManagementService::loadComputeResources);
        });
    }

    private void initializeAddHostButton() {
        addHostButton.setOnAction(buttonEvent -> {
            hostFormPane.showPane();
        });
    }

    private void initializeDeleteSelectedHostMenuItem() {
        deleteSelectedHostsMenuItem.disableProperty()
                .bind(statusBarState.disableDeleteHostMenuItemProperty());
        deleteSelectedHostsMenuItem.setOnAction(event -> {
            alertUtility.confirmAlert("Delete Hosts", "Are you sure you want to delete selected hosts?", () -> {
                hostManagementService.deleteSelectedHosts();
            });
        });
    }

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
