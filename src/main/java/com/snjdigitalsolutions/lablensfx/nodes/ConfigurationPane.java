package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.application.ChangeListenerRegistry;
import com.snjdigitalsolutions.lablensfx.nodes.tableview.ConfigurationPathTableView;
import com.snjdigitalsolutions.lablensfx.nodes.tableview.PathFilesTableView;
import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.lablensfx.service.node.ConfigurationPaneService;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
    @FXML
    private AnchorPane filesAnchorPane;
    private final ConfigurationPathTableView configurationPathTableView;
    private final PathFilesTableView pathFilesTableView;

    private final double splitPaneDividerPosition = 0.5;
    private final ChangeListenerRegistry changeListenerRegistry;
    private final ConfigurationPaneService configurationPaneService;

    public ConfigurationPane(@Value("classpath:/fxml/ConfigurationPane.fxml") Resource fxml,
                             ConfigurationPathTableView configurationPathTableView,
                             PathFilesTableView pathFilesTableView,
                             ChangeListenerRegistry changeListenerRegistry,
                             ConfigurationPaneService configurationPaneService
    )
    {
        this.configurationPathTableView = configurationPathTableView;
        this.pathFilesTableView = pathFilesTableView;
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

        VBox tableContainer = new VBox();
        filesAnchorPane.getChildren().add(tableContainer);
        AnchorPane.setLeftAnchor(tableContainer, 0D);
        AnchorPane.setTopAnchor(tableContainer, 0D);
        AnchorPane.setRightAnchor(tableContainer, 0D);
        AnchorPane.setBottomAnchor(tableContainer, 0D);
        tableContainer.setPadding(new Insets(5));

        tableContainer.getChildren().add(pathFilesTableView);
        VBox.setVgrow(pathFilesTableView, Priority.ALWAYS);

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
            configurationPaneService.addButtonAction(filePathTextField);
        });
    }

    private void initializePathTable() {
        leftVBox.getChildren()
                .add(configurationPathTableView);
        VBox.setVgrow(configurationPathTableView, Priority.ALWAYS);
        ChangeListener<ConfigurationPath> changeListener = (obj, oldVal, newVal) -> {
            deleteButton.setDisable(newVal == null);
            if (newVal != null){
                configurationPaneService.listFilesForConfigurationPath(newVal);
            }
        };
        configurationPathTableView.addSelectedItemChangeListener(configurationPathTableView.selectedItemProperty(), changeListener);
    }
}
