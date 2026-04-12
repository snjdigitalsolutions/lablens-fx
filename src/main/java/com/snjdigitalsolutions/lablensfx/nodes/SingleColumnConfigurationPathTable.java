package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.ConfigurationPath;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class SingleColumnConfigurationPathTable extends TableView<ConfigurationPath> implements SpringInitializableNode {

    private final ObservableList<ConfigurationPath> paths = FXCollections.observableArrayList();

    @Override
    public void performIntialization() {
        setFocusTraversable(false);
        TableColumn<ConfigurationPath, String> pathColumn = getConfigurationPathStringTableColumn();
        getStyleClass().add("no-header");
        pathColumn.prefWidthProperty().bind(widthProperty().subtract(3));
        getColumns().add(pathColumn);
        setMaxHeight(150);
        setItems(paths);
        setPlaceholder(new Label("No configuration paths tracked"));
    }

    private TableColumn<ConfigurationPath, String> getConfigurationPathStringTableColumn() {
        TableColumn<ConfigurationPath, String> pathColumn = new TableColumn<>("Path");
        pathColumn.setCellValueFactory(path -> path.getValue()
                .configurationPath());
        return pathColumn;
    }

    public void setConfigurationPaths(List<ConfigurationPath> paths){
        this.paths.clear();
        this.paths.addAll(paths);
    }
}
