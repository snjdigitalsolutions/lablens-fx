package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.lablensfx.nodes.DashboardPane;
import com.snjdigitalsolutions.lablensfx.nodes.HostFormPane;
import com.snjdigitalsolutions.lablensfx.nodes.HostPane;
import com.snjdigitalsolutions.lablensfx.nodes.PassphraseDialog;
import com.snjdigitalsolutions.lablensfx.properties.IpAddressProperties;
import com.snjdigitalsolutions.lablensfx.properties.SshProperties;
import com.snjdigitalsolutions.lablensfx.properties.StatusBarProperties;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.lablensfx.service.PassPhraseMode;
import com.snjdigitalsolutions.lablensfx.shapes.SshStatus;
import com.snjdigitalsolutions.lablensfx.shapes.StatusIndicator;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.ButtonUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.TooltipGenerator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class LabLensFxBootReadyController implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabLensFxBootReadyController.class);
    private final TooltipGenerator tooltipGenerator;

    private enum IpState {
        SHOW("Hide IP Addresses"),
        HIDE("Show IP Addresses");

        private final String menuItemMessage;

        IpState(String menuItemMessage){
            this.menuItemMessage = menuItemMessage;
        }
    };

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
    private MenuItem deleteSelectedHostsMenuItem;
    @FXML
    private MenuItem showHideIpMenuItem;
    @FXML
    private FontAwesomeIconView showHideIpIconView;

    private IpState currentState = IpState.SHOW;

    private final ObjectProvider<StatusIndicator> statusIndicatorProvider;
    private final HostPane hostPane;
    private final HostFormPane hostFormPane;
    private final StatusBarProperties statusBarProperties;
    private final SshProperties sshProperties;
    private final DashboardPane dashboardPane;
    private final HostManagementService hostManagementService;
    private final PassphraseDialog passphraseDialog;
    private final IpAddressProperties ipAddressProperties;
    private final ButtonUtility buttonUtility;

    private StatusIndicator indicator;

    public LabLensFxBootReadyController(ObjectProvider<StatusIndicator> statusIndicatorProvider,
                                        HostPane hostPane,
                                        HostFormPane hostFormPane,
                                        StatusBarProperties statusBarProperties,
                                        SshProperties sshProperties,
                                        DashboardPane dashboardPane,
                                        HostManagementService hostManagementService,
                                        ButtonUtility buttonUtility,
                                        TooltipGenerator tooltipGenerator,
                                        PassphraseDialog passphraseDialog,
                                        IpAddressProperties ipAddressProperties) {
        this.statusIndicatorProvider = statusIndicatorProvider;
        this.hostPane = hostPane;
        this.hostFormPane = hostFormPane;
        this.statusBarProperties = statusBarProperties;
        this.sshProperties = sshProperties;
        this.dashboardPane = dashboardPane;
        this.hostManagementService = hostManagementService;
        this.buttonUtility = buttonUtility;
        this.tooltipGenerator = tooltipGenerator;
        this.passphraseDialog = passphraseDialog;
        this.ipAddressProperties = ipAddressProperties;
    }

    @Override
    public void performIntialization() {
        rootPane.setLeft(hostPane);
        rootPane.setCenter(dashboardPane);
        statusBar.textProperty().bind(statusBarProperties.statusProperty());
        initializeViewButtons();
        initializeSshCredentialIndicator();
        initializeDeleteSelectedHostMenuItem();
        initializeAddHostButton();
        initializeSshButton();
        initializeShowHideIpMenuItem();
        sshProperties.passPhraseModeProperty().addListener((obj, oldVal, newVal) -> {
            if (newVal.equals(PassPhraseMode.PROVIDED)) {
                indicator.hostSshStatusProperty()
                        .setValue(SshStatus.ONLINE);
            } else {
                indicator.hostSshStatusProperty()
                        .setValue(SshStatus.OFFLINE);
            }
        });

    }

    private void initializeViewButtons() {
        configButton.setDisable(true);
        logButton.setDisable(true);
        timelineButton.setDisable(true);
        statusBarProperties.numberOfSelectedHostsProperty()
                .addListener((obj, oldVal, newVal) -> {
                    if (newVal.intValue() == 1) {
                        configButton.setDisable(false);
                        logButton.setDisable(false);
                        timelineButton.setDisable(false);
                    } else {
                        configButton.setDisable(true);
                        logButton.setDisable(true);
                        timelineButton.setDisable(true);
                    }
                });
    }

    private void initializeSshCredentialIndicator() {
        indicator = statusIndicatorProvider.getObject();
        indicator.hostSshStatusProperty().setValue(SshStatus.OFFLINE);
        statusBar.getRightItems().add(indicator);
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
        deleteSelectedHostsMenuItem.disableProperty().bind(statusBarProperties.disableDeleteHostMenuItemProperty());
        deleteSelectedHostsMenuItem.setOnAction(event -> {
            hostManagementService.deleteSelectedHosts();
        });
    }

    private void initializeShowHideIpMenuItem() {
        showHideIpMenuItem.setText(IpState.SHOW.menuItemMessage);
        showHideIpIconView.setIcon(FontAwesomeIcon.EYE_SLASH);
        showHideIpMenuItem.setOnAction(event -> {
            if (currentState.equals(IpState.SHOW)){
                showHideIpIconView.setIcon(FontAwesomeIcon.EYE);
                currentState = IpState.HIDE;
                showHideIpMenuItem.setText(IpState.HIDE.menuItemMessage);
                ipAddressProperties.showIpPropertyProperty().setValue(false);
            } else {
                showHideIpIconView.setIcon(FontAwesomeIcon.EYE_SLASH);
                currentState = IpState.SHOW;
                showHideIpMenuItem.setText(IpState.SHOW.menuItemMessage);
                ipAddressProperties.showIpPropertyProperty().setValue(true);
            }
        });
    }

}
