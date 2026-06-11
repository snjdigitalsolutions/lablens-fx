package com.snjdigitalsolutions.lablensfx.nodes.tableview;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.FileStorage;
import com.snjdigitalsolutions.lablensfx.orm.FileSystemObject;
import com.snjdigitalsolutions.lablensfx.orm.model.FileSystemObjectModel;
import com.snjdigitalsolutions.lablensfx.service.FilePersistenceService;
import com.snjdigitalsolutions.lablensfx.service.HostManagementService;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.AlertUtility;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class PathFilesTableView extends TableView<FileSystemObjectModel> implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFilesTableView.class);
    @Setter
    private HostManagementService hostManagementService;
    @Setter
    private FilePersistenceService filePersistenceService;
    private final AlertUtility alertUtility;

    /**
     * Creates the files table view backed by the given persistence service.
     *
     * @param filePersistenceService the service used to resolve file persistence state
     */
    public PathFilesTableView(FilePersistenceService filePersistenceService,
                              AlertUtility alertUtility
    ) {
        this.filePersistenceService = filePersistenceService;
        this.alertUtility = alertUtility;
    }

    /**
     * Configures file-name and track-file columns with their cell factories.
     */
    @Override
    public void performIntialization() {
        setFocusTraversable(false);
        TableColumn<FileSystemObjectModel, Boolean> trackColumn = getTrackFileTableColumn();
        trackColumn.prefWidthProperty()
                .bind(widthProperty().multiply(.2));
        TableColumn<FileSystemObjectModel, String> pathColumn = getFileNameTableColumn();
        pathColumn.prefWidthProperty()
                .bind(widthProperty().multiply(.8)
                              .subtract(3));
        getColumns().add(trackColumn);
        getColumns().add(pathColumn);
        setItems(FXCollections.observableArrayList());
        setPlaceholder(new Label("No file path selected"));
    }

    /**
     * Returns the currently selected file-system object model, or {@code null} if none is selected.
     *
     * @return the selected {@link FileSystemObjectModel}
     */
    public FileSystemObjectModel getSelectedItem() {
        return getSelectionModel().getSelectedItem();
    }

    /**
     * Removes the currently selected row from the table and backing list.
     */
    public void removeCurrentlySelectedItem() {
        getItems().remove(getSelectedItem());
    }

    /**
     * Clears the current row selection without removing items.
     */
    public void clearSelection() {
        getSelectionModel().clearSelection();
    }

    /**
     * Removes all file rows from the table.
     */
    public void clearItems() {
        getItems().clear();
    }

    /**
     * Appends a file-system object model row to the table.
     *
     * @param fileSystemObject the model to add
     */
    public void addItem(FileSystemObjectModel fileSystemObject) {
        getItems().add(fileSystemObject);
    }

    /**
     * Exposes the selected-item property for external binding.
     *
     * @return the selected-item observable property
     */
    public ObservableValue<FileSystemObjectModel> selectedItemProperty() {
        return getSelectionModel().selectedItemProperty();
    }

    /**
     * Returns the file-name column for external configuration.
     *
     * @return the file-name {@link TableColumn}
     */
    @NonNull
    private TableColumn<FileSystemObjectModel, String> getFileNameTableColumn() {
        TableColumn<FileSystemObjectModel, String> filenameColumn = new TableColumn<>("Filename");
        filenameColumn.setCellValueFactory(object -> object.getValue()
                .fileNameProperty());
        filenameColumn.setCellFactory(column -> new TableCell<>() {
            private final Label fileNameLabel = new Label();

            @Override
            protected void updateItem(String item,
                                      boolean empty
            )
            {
                super.updateItem(item, empty);
                setGraphic(null);
                if (item != null) {
                    FileSystemObjectModel model = getTableView().getItems()
                            .get(getIndex());
                    if (model.isNonExistantFile()) {
                        fileNameLabel.setStyle("-fx-text-fill: orange");
                    } else {
                        fileNameLabel.setStyle("-fx-text-fill: black");
                    }
                    fileNameLabel.setText(item);
                    setGraphic(fileNameLabel);
                }
            }

        });
        return filenameColumn;
    }

    /**
     * Returns the track-file toggle column for external configuration.
     *
     * @return the track-file {@link TableColumn}
     */
    @NonNull
    private TableColumn<FileSystemObjectModel, Boolean> getTrackFileTableColumn() {
        TableColumn<FileSystemObjectModel, Boolean> trackFileColumn = new TableColumn<>("Track");
        trackFileColumn.setCellValueFactory(trackFile -> trackFile.getValue()
                .trackFileProperty());
        trackFileColumn.setCellFactory(column -> new TableCell<>() {
            private final CheckBox trackCheckBox = new CheckBox();
            private final HBox cellBox = new HBox();

            {
                trackCheckBox.setFocusTraversable(false);
                cellBox.setMaxWidth(Double.MAX_VALUE);
                cellBox.setAlignment(Pos.CENTER);
                cellBox.getChildren()
                        .add(trackCheckBox);
                trackCheckBox.setOnAction(event -> {
                    FileSystemObjectModel model = getTableView().getItems()
                            .get(getIndex());
                    Optional<ComputeResource> optComputeResource = hostManagementService.getComputerResourceById(model.getComputeResourceID());
                    if (optComputeResource.isPresent()) {
                        // Get all file system object with same parent path
                        List<FileSystemObject> filesFromPath = optComputeResource.get()
                                .getFileSystemObjects()
                                .stream()
                                .filter(fso -> fso.getParentPath()
                                        .equalsIgnoreCase(model.getParentPath()))
                                .toList();
                        // Get file object by iterating all files in parent path and matching name
                        Optional<FileSystemObject> optFileObject = filesFromPath.stream()
                                .filter(file -> file.getFileName()
                                        .equalsIgnoreCase(model.getFileName()))
                                .findFirst();
                        // Change selected status
                        optFileObject.ifPresent(fileSystemObject -> {
                            fileSystemObject.setTrackFile(trackCheckBox.isSelected());
                            //Remove files from database when file not selected
                            if (!trackCheckBox.isSelected()) {
                                AtomicBoolean confirmedYes = new AtomicBoolean(false);
                                alertUtility.confirmAlert("Remove Configuration", "Removing the configuration from tracking status will remove history from the database for this file. Continue?", () -> {
                                    confirmedYes.set(true);
                                    String absoluteFilePath = fileSystemObject.getParentPath() + "/" + fileSystemObject.getFileName();
                                    List<FileStorage> fileStorageList = optComputeResource.get()
                                            .getFileStorages()
                                            .stream()
                                            .filter(fso -> fso.getAbsolutePath()
                                                    .equals(absoluteFilePath))
                                            .toList();
                                    if (!fileStorageList.isEmpty()) {
                                        for (FileStorage fileStorage : fileStorageList) {
                                            optComputeResource.get()
                                                    .getFileStorages()
                                                    .remove(fileStorage);
                                        }
                                    }
                                });
                                if (!confirmedYes.get()) {
                                    trackCheckBox.setSelected(true);
                                }
                                fileSystemObject.setTrackFile(trackCheckBox.isSelected());
                            }
                        });
                        hostManagementService.updateComputeResource(optComputeResource.get());
                        filePersistenceService.updateConfigurationFilePersistence();
                    }

                    //TODO Start a cleanup thread
                    Map<ComputeResource, List<FileSystemObject>> unpersistedFiles = filePersistenceService.findUnpersistedTrackedFiles();
                    LOGGER.debug("Found unpersisted files...");
                });
            }

            @Override
            protected void updateItem(Boolean item,
                                      boolean empty
            )
            {
                super.updateItem(item, empty);
                setGraphic(null);
                if (item != null) {
                    trackCheckBox.setSelected(item);
                    setGraphic(cellBox);
                }
            }
        });
        return trackFileColumn;
    }


}
