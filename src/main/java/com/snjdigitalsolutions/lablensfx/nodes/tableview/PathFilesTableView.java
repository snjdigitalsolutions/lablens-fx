package com.snjdigitalsolutions.lablensfx.nodes.tableview;

import com.snjdigitalsolutions.lablensfx.application.ChangeListenerRegistry;
import com.snjdigitalsolutions.lablensfx.orm.FileSystemObject;
import com.snjdigitalsolutions.lablensfx.orm.model.FileSystemObjectModel;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PathFilesTableView extends TableView<FileSystemObjectModel> implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFilesTableView.class);
    private final ChangeListenerRegistry changeListenerRegistry;

    public PathFilesTableView(ChangeListenerRegistry changeListenerRegistry) {
        this.changeListenerRegistry = changeListenerRegistry;
    }

    @Override
    public void performIntialization() {
        setFocusTraversable(false);
        TableColumn<FileSystemObjectModel, Boolean> trackColumn = getTrackFileTableColumn();
        trackColumn.prefWidthProperty().bind(widthProperty().multiply(.2));
        TableColumn<FileSystemObjectModel, String> pathColumn = getFileNameTableColumn();
        pathColumn.prefWidthProperty()
                .bind(widthProperty().multiply(.8)
                              .subtract(3));
        getColumns().add(trackColumn);
        getColumns().add(pathColumn);
        setItems(FXCollections.observableArrayList());
        setPlaceholder(new Label("No file path selected"));
    }

    public FileSystemObjectModel getSelectedItem() {
        return getSelectionModel().getSelectedItem();
    }

    public void removeCurrentlySelectedItem() {
        getItems().remove(getSelectedItem());
    }

    public void clearSelection() {
        getSelectionModel().clearSelection();
    }

    public void clearItems() {
        getItems().clear();
    }

    public void addItem(FileSystemObjectModel fileSystemObject) {
        getItems().add(fileSystemObject);
    }

    public ObservableValue<FileSystemObjectModel> selectedItemProperty() {
        return getSelectionModel().selectedItemProperty();
    }

    @NonNull
    private TableColumn<FileSystemObjectModel, String> getFileNameTableColumn() {
        TableColumn<FileSystemObjectModel, String> filenameColumn = new TableColumn<>("Filename");
        filenameColumn.setCellValueFactory(object -> object.getValue()
                .fileNameProperty());
        return filenameColumn;
    }

    @NonNull
    private TableColumn<FileSystemObjectModel, Boolean> getTrackFileTableColumn() {
        TableColumn<FileSystemObjectModel, Boolean> trackFileColumn = new TableColumn<>("Track");
        trackFileColumn.setCellValueFactory(trackFile -> trackFile.getValue()
                .trackFileProperty());
        trackFileColumn.setCellFactory( column -> new TableCell<>() {
            private final CheckBox trackCheckBox = new CheckBox();
            private final HBox cellBox = new HBox();

            {
                trackCheckBox.setFocusTraversable(false);
                cellBox.setMaxWidth(Double.MAX_VALUE);
                cellBox.setAlignment(Pos.CENTER);
                cellBox.getChildren().add(trackCheckBox);
                trackCheckBox.selectedProperty().addListener((obj, oldVal, newVal) -> {
                    LOGGER.debug("CheckBox is selected: {}", newVal);
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
