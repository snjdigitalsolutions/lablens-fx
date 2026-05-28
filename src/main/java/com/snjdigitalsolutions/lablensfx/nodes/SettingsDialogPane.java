package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.Setting;
import com.snjdigitalsolutions.lablensfx.repository.SettingRepository;
import com.snjdigitalsolutions.lablensfx.setting.Interval;
import com.snjdigitalsolutions.lablensfx.setting.SettingType;
import com.snjdigitalsolutions.springbootutilityfx.node.CloseableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeUtility;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class SettingsDialogPane extends AnchorPane implements SpringInitializableNode, CloseableNode {

    @FXML
    private ComboBox<Interval> intervalComboBox;
    @FXML
    private TextField snapshotIntervalValueTextField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button applyButton;
    private final SettingRepository settingRepository;
    private final NodeUtility nodeUtility;

    public SettingsDialogPane(@Value("classpath:/fxml/SettingsPane.fxml") Resource fxml,
                              SettingRepository settingRepository,
                              NodeUtility nodeUtility
    ) {
        this.settingRepository = settingRepository;
        this.nodeUtility = nodeUtility;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        StringConverter<Interval> converter = new StringConverter<>() {
            @Override
            public String toString(Interval object) {
                if (object != null){
                    return object.displayValue();
                }
                return "";
            }

            @Override
            public Interval fromString(String string) {
                for (Interval interval : Interval.values()){
                    if (interval.displayValue().contentEquals(string)){
                        return interval;
                    }
                }
                return null;
            }
        };
        for (Interval interval : Interval.values()){
            intervalComboBox.getItems().add(interval);
        }
        Optional<Setting> setting = settingRepository.findBySettingName(SettingType.SNAPSHOT_INTERVAL.getName());
        if (setting.isPresent()){
            String matchValue = setting.get().getStringValue();
            for (Interval interval: Interval.values()){
                if (interval.displayValue().contentEquals(matchValue)){
                    intervalComboBox.getSelectionModel().select(interval);
                }
            }
        }
        setting = settingRepository.findBySettingName(SettingType.SNAPSHOT_INTERVAL_VALUE.getName());
        setting.ifPresent(value -> snapshotIntervalValueTextField.setText(value.getStringValue()));
        intervalComboBox.setConverter(converter);
        applyButton.prefWidthProperty().bind(cancelButton.widthProperty());
        cancelButton.setOnAction(this::close);
        applyButton.setOnAction(event -> {
            //TODO complete the work when applied
            this.close(event);
        });
    }

    @Override
    public void close(ActionEvent event) {
        nodeUtility.closeNode(event);
    }
}
