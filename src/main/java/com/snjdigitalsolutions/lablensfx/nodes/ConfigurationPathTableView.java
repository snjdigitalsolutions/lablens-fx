package com.snjdigitalsolutions.lablensfx.nodes;

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

    public ConfigurationPathTableView(ChangeListenerRegistry changeListenerRegistry) {
        this.changeListenerRegistry = changeListenerRegistry;
    }

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

    public ConfigurationPath getSelectedItem(){
        return getSelectionModel().getSelectedItem();
    }

    public void removeCurrentlySelectedItem(){
        getItems().remove(getSelectedItem());
    }

    public void clearSelection(){
        getSelectionModel().clearSelection();
    }

    public void clearItems(){
        getItems().clear();
    }

    public void addItem(ConfigurationPath path) {
        getItems().add(path);
    }

    public void addSelectedItemChangeListener(ObservableValue<ConfigurationPath> observableValue, ChangeListener<ConfigurationPath> changeListener){
        changeListenerRegistry.add(this, observableValue, changeListener);
    }

    public ObservableValue<ConfigurationPath> selectedItemProperty(){
        return getSelectionModel().selectedItemProperty();
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
