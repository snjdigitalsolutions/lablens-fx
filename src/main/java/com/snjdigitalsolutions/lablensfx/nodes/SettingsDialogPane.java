package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.Setting;
import com.snjdigitalsolutions.lablensfx.repository.SettingRepository;
import com.snjdigitalsolutions.lablensfx.setting.Interval;
import com.snjdigitalsolutions.lablensfx.setting.SettingType;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.fxml.FXML;
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
public class SettingsDialogPane extends AnchorPane implements SpringInitializableNode {

    @FXML
    private ComboBox<Interval> intervalComboBox;
    @FXML
    private TextField snapshotIntervalValueTextField;
    private final SettingRepository settingRepository;

    public SettingsDialogPane(@Value("classpath:/fxml/SettingsPane.fxml") Resource fxml,
                              SettingRepository settingRepository
    ) {
        this.settingRepository = settingRepository;
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
    }
}
