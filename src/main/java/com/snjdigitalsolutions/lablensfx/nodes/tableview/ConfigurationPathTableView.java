package com.snjdigitalsolutions.lablensfx.nodes.tableview;

import com.snjdigitalsolutions.lablensfx.application.ChangeListenerRegistry;
import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationPathTableView extends TableView<ConfigurationPath> implements SpringInitializableNode {

    private final ChangeListenerRegistry changeListenerRegistry;

    /**
     * Creates the table view with the registry used to track listeners for later disposal.
     *
     * @param changeListenerRegistry the change listener registry
     */
    public ConfigurationPathTableView(ChangeListenerRegistry changeListenerRegistry) {
        this.changeListenerRegistry = changeListenerRegistry;
    }

    /**
     * Configures table columns and their cell factories.
     */
    @Override
    public void performIntialization() {
        setFocusTraversable(false);
        TableColumn<ConfigurationPath, String> pathColumn = getConfigurationPathStringTableColumn();
        pathColumn.prefWidthProperty()
                .bind(widthProperty().multiply(.6));
        TableColumn<ConfigurationPath, Boolean> elevateColumn = getConfigurationPathElevationrequiredTableColumn();
        elevateColumn.prefWidthProperty()
                .bind(widthProperty().multiply(.4)
                              .subtract(3));
        getColumns().add(pathColumn);
        getColumns().add(elevateColumn);
        setItems(FXCollections.observableArrayList());
    }

    /**
     * Returns the currently selected configuration path, or {@code null} if none is selected.
     *
     * @return the selected {@link ConfigurationPath}
     */
    public ConfigurationPath getSelectedItem(){
        return getSelectionModel().getSelectedItem();
    }

    /**
     * Removes the currently highlighted row from the table and from the backing list.
     */
    public void removeCurrentlySelectedItem(){
        getItems().remove(getSelectedItem());
    }

    /**
     * Clears the table's current row selection without removing items.
     */
    public void clearSelection(){
        getSelectionModel().clearSelection();
    }

    /**
     * Removes all rows from the table.
     */
    public void clearItems(){
        getItems().clear();
    }

    /**
     * Appends a configuration path entry to the table.
     *
     * @param path the path to add
     */
    public void addItem(ConfigurationPath path) {
        getItems().add(path);
    }

    /**
     * Registers an external listener on the selected-item property.
     *
     * @param observableValue the observable to attach to (typically the selection model)
     * @param changeListener  the listener to notify on change
     */
    public void addSelectedItemChangeListener(ObservableValue<ConfigurationPath> observableValue, ChangeListener<ConfigurationPath> changeListener){
        changeListenerRegistry.add(this, observableValue, changeListener);
    }

    /**
     * Exposes the selected-item property for external binding.
     *
     * @return the selected-item observable property
     */
    public ObservableValue<ConfigurationPath> selectedItemProperty(){
        return getSelectionModel().selectedItemProperty();
    }

    /**
     * Returns the path-string column for external configuration.
     *
     * @return the path string {@link TableColumn}
     */
    @NonNull
    private TableColumn<ConfigurationPath, String> getConfigurationPathStringTableColumn() {
        TableColumn<ConfigurationPath, String> pathColumn = new TableColumn<>("Path");
        pathColumn.setCellValueFactory(path -> path.getValue()
                .configurationPath());
        return pathColumn;
    }

    /**
     * Returns the elevation-required column for external configuration.
     *
     * @return the elevation-required {@link TableColumn}
     */
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
            protected void updateItem(Boolean item,
                                      boolean empty
            )
            {
                super.updateItem(item, empty);
                setGraphic(null);
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
