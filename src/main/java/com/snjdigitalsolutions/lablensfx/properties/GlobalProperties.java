package com.snjdigitalsolutions.lablensfx.properties;

import com.snjdigitalsolutions.lablensfx.nodes.HostPanel;
import com.snjdigitalsolutions.lablensfx.nodes.SummaryPanel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
@Getter
public class GlobalProperties {

    private final IntegerProperty numberOfHostsProperty = new SimpleIntegerProperty(0);
    private final ObjectProperty<SummaryPanel> numberOfHostsPanelProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<SummaryPanel> numberOfHostsOnlinePanelProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<SummaryPanel> numberOfConfigurationChangePanelProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<SummaryPanel> numberOfLegErrorsPanelProperty = new SimpleObjectProperty<>();
    private final ListProperty<HostPanel> selectedHostPanelListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

}
