package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.application.ChangeListenerRegistry;
import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.lablensfx.service.node.ConfigurationPaneService;
import com.snjdigitalsolutions.lablensfx.utility.FilePathValidator;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationPane extends AnchorPane implements SpringInitializableNode {

    @FXML
    private SplitPane splitPane;
    @FXML
    private TextField filePathTextField;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private VBox leftVBox;
    private final ConfigurationPathTableView configurationPathTableView;

    private final double splitPaneDividerPosition = 0.5;
    private final FilePathValidator filePathValidator;
    private final AlertUtility alertUtility;
    private final ChangeListenerRegistry changeListenerRegistry;
    private final ConfigurationPaneService configurationPaneService;

    public ConfigurationPane(@Value("classpath:/fxml/ConfigurationPane.fxml") Resource fxml,
                             ConfigurationPathTableView configurationPathTableView,
                             FilePathValidator filePathValidator,
                             AlertUtility alertUtility,
                             ChangeListenerRegistry changeListenerRegistry,
                             ConfigurationPaneService configurationPaneService
    )
    {
        this.configurationPathTableView = configurationPathTableView;
        this.filePathValidator = filePathValidator;
        this.alertUtility = alertUtility;
        this.changeListenerRegistry = changeListenerRegistry;
        this.configurationPaneService = configurationPaneService;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        initializeDivider();
        initializeAddButton();
        initializeDeleteButton();
        initializePathTable();
    }

    private void initializeDeleteButton() {
        deleteButton.setOnAction(event -> {
            configurationPaneService.removeConfigurationPathFromSelectedResource(configurationPathTableView.getSelectedItem());
            configurationPathTableView.removeCurrentlySelectedItem();
            configurationPathTableView.clearSelection();
        });
    }

    private void initializeDivider() {
        ChangeListener<Number> dividerPositionChangeListener = (obj, oldVal, newVal) -> {
            if (newVal.doubleValue() != splitPaneDividerPosition) {
                splitPane.setDividerPosition(0, splitPaneDividerPosition);
            }
        };
        changeListenerRegistry.add(this, splitPane.getDividers()
                .getFirst()
                .positionProperty(), dividerPositionChangeListener);
    }

    private void initializeAddButton() {
        addButton.setOnAction(event -> {
            if (filePathValidator.isValid(filePathTextField.getText())) {
                ConfigurationPath path = new ConfigurationPath();
                path.setConfigurationPath(filePathTextField.getText());
                path.setRequiresElevation(false);
                path.setElevationCheckComplete(false);
                if (!configurationPaneService.addPathToSelectedResource(path)) {
                    alertUtility.warningAlert("Not Added", "Unable to add configuration path to host. Check for duplicate entry");
                } else {
                    filePathTextField.clear();
                    configurationPaneService.loadExistingPaths();
                }
            } else {
                alertUtility.warningAlert("Invalid Path", "The path entered is not a valid system path.");
            }
        });
    }

    private void initializePathTable() {
        leftVBox.getChildren()
                .add(configurationPathTableView);
        VBox.setVgrow(configurationPathTableView, Priority.ALWAYS);
        ChangeListener<ConfigurationPath> changeListener = (obj, oldVal, newVal) -> {
            deleteButton.setDisable(newVal == null);
        };
        configurationPathTableView.addSelectedItemChangeListener(configurationPathTableView.selectedItemProperty(), changeListener);
    }
}
