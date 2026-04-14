package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.application.ChangeListenerRegistry;
import com.snjdigitalsolutions.lablensfx.orm.model.FileSystemObjectModel;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PathFilesTableView extends TableView<FileSystemObjectModel> implements SpringInitializableNode {

    private final ChangeListenerRegistry changeListenerRegistry;

    public PathFilesTableView(ChangeListenerRegistry changeListenerRegistry) {
        this.changeListenerRegistry = changeListenerRegistry;
    }

    @Override
    public void performIntialization() {
        setFocusTraversable(false);
        TableColumn<FileSystemObjectModel, String> pathColumn = getFileNameTableColumn();
        pathColumn.prefWidthProperty()
                .bind(widthProperty().multiply(1)
                              .subtract(3));
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

}
