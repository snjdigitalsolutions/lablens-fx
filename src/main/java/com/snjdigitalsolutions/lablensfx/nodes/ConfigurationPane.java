package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.lablensfx.repository.ComputeResourceRepository;
import com.snjdigitalsolutions.lablensfx.state.ComputeResourceState;
import com.snjdigitalsolutions.lablensfx.utility.FilePathValidator;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
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
    private final ComputeResourceState computeResourceState;
    private final ComputeResourceRepository computeResourceRepository;

    public ConfigurationPane(@Value("classpath:/fxml/ConfigurationPane.fxml") Resource fxml,
                             FilePathValidator filePathValidator,
                             AlertUtility alertUtility,
                             ComputeResourceState computeResourceState,
                             ComputeResourceRepository computeResourceRepository) {
        this.filePathValidator = filePathValidator;
        this.alertUtility = alertUtility;
        this.computeResourceState = computeResourceState;
        this.computeResourceRepository = computeResourceRepository;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        initializeDivider();
        initializeAddButton();
        initializePathTable();
    }

    public void loadExistingPaths() {
        if (computeResourceState.getSelectedResources().size() == 1){
            selectedPathsTable.getItems().clear();
            computeResourceState.getSelectedResources().getFirst().getConfigurationPaths().forEach(path -> {
                selectedPathsTable.getItems().add(path);
            });
        } else {
            alertUtility.warningAlert("No Selection", "No compute resource is selected");
        }
    }

    private void initializeDivider() {
        splitPane.getDividers()
                .getFirst()
                .positionProperty()
                .addListener((obj, oldVal, newVal) -> {
                    if (newVal.doubleValue() != splitPaneDividerPosition) {
                        splitPane.setDividerPosition(0, splitPaneDividerPosition);
                    }
                });
    }

    private void initializeAddButton() {
        addButton.setOnAction(event -> {
            if (computeResourceState.getSelectedResources().size() == 1){
                if (filePathValidator.isValid(filePathTextField.getText())) {
                    ComputeResource selectedResource = computeResourceState.getSelectedResources().getFirst();
                    if (selectedResource.getConfigurationPaths()
                            .isEmpty()){
                        ConfigurationPath path = new ConfigurationPath();
                        path.setConfigurationPath(filePathTextField.getText());
                        path.setRequiresElevation(false);
                        selectedResource.getConfigurationPaths().add(path);
                        path.setComputeResource(selectedResource);
                        computeResourceRepository.save(selectedResource);
                        selectedPathsTable.getItems().add(path);
                    } else {
                        computeResourceState.getSelectedResources().getFirst().getConfigurationPaths().forEach(path -> {

                        });
                    }
                } else {
                    alertUtility.warningAlert("Invalid Path", "The path entered is not a valid system path.");
                }
            } else {
                alertUtility.warningAlert("Incorrect Selection", "One and only one host selection must be made.");
            }
        });
    }

    private void initializePathTable() {
        selectedPathsTable.setFocusTraversable(false);
        TableColumn<ConfigurationPath, String> pathColumn = getConfigurationPathStringTableColumn();
        pathColumn.prefWidthProperty().bind(selectedPathsTable.widthProperty().multiply(.6));
        TableColumn<ConfigurationPath, Boolean> elevateColumn = getConfigurationPathElevationrequiredTableColumn();
        elevateColumn.prefWidthProperty().bind(selectedPathsTable.widthProperty().multiply(.4).subtract(2));
        selectedPathsTable.getColumns().add(pathColumn);
        selectedPathsTable.getColumns().add(elevateColumn);
        selectedPathsTable.setItems(FXCollections.observableArrayList());
    }

    @NonNull
    private TableColumn<ConfigurationPath, String> getConfigurationPathStringTableColumn() {
        TableColumn<ConfigurationPath, String> pathColumn = new TableColumn<>("Path");
        pathColumn.setCellValueFactory(path -> path.getValue()
                .configurationPath());
        return pathColumn;
    }

    @NonNull
    private TableColumn<ConfigurationPath, Boolean> getConfigurationPathElevationrequiredTableColumn() {
        TableColumn<ConfigurationPath, Boolean> privilegeColumn = new TableColumn<>("Privilege");
        privilegeColumn.setCellValueFactory(path -> path.getValue()
                .requiresElevation());
        privilegeColumn.setCellFactory(column -> new TableCell<>() {
            private final FontAwesomeIconView privilegeIcon = new FontAwesomeIconView(FontAwesomeIcon.UNLOCK);
            private final Label label = new Label();
            {
                label.setMaxWidth(Double.MAX_VALUE);
                label.setAlignment(Pos.CENTER);
                label.setGraphic(privilegeIcon);
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    if (item) {
                        privilegeIcon.setIcon(FontAwesomeIcon.LOCK);
                    } else {
                        privilegeIcon.setIcon(FontAwesomeIcon.UNLOCK);
                    }
                    setGraphic(label);
                }
            }
        });
        return privilegeColumn;
    }

}
