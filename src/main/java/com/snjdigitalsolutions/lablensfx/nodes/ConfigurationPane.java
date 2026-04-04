package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.lablensfx.utility.FilePathValidator;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
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
    private TableView<ConfigurationPath> selectedPathsTable;

    private final double splitPaneDividerPosition = 0.5;
    private final FilePathValidator filePathValidator;
    private final AlertUtility alertUtility;

    public ConfigurationPane(@Value("classpath:/fxml/ConfigurationPane.fxml") Resource fxml, FilePathValidator filePathValidator, AlertUtility alertUtility) {
        this.filePathValidator = filePathValidator;
        this.alertUtility = alertUtility;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        splitPane.getDividers()
                .get(0)
                .positionProperty()
                .addListener((obj, oldVal, newVal) -> {
                    if (newVal.doubleValue() != splitPaneDividerPosition) {
                        splitPane.setDividerPosition(0, splitPaneDividerPosition);
                    }
                });
        addButton.setOnAction(event -> {
            if (filePathValidator.isValid(filePathTextField.getText())) {
                ConfigurationPath newPath = new ConfigurationPath();
                newPath.setConfigurationPath(filePathTextField.getText());
                selectedPathsTable.getItems().add(newPath);
            } else {
                alertUtility.warningAlert("Invalid Path", "The path entered is not a valid system path.");
            }

            TableColumn<ConfigurationPath, String> pathColumn = new TableColumn<>("Path");
            pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
            selectedPathsTable.getColumns().add(pathColumn);
        });
    }

    private void validateFilePath() {

    }

}
