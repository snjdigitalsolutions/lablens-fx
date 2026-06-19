package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.orm.Setting;
import com.snjdigitalsolutions.lablensfx.repository.SettingRepository;
import com.snjdigitalsolutions.lablensfx.service.ScheduledTaskType;
import com.snjdigitalsolutions.lablensfx.service.TaskSchedulingService;
import com.snjdigitalsolutions.lablensfx.setting.Interval;
import com.snjdigitalsolutions.lablensfx.setting.SettingType;
import com.snjdigitalsolutions.lablensfx.state.SettingState;
import com.snjdigitalsolutions.lablensfx.task.ConfigurationChangeCheckTask;
import com.snjdigitalsolutions.lablensfx.utility.ComboBoxResizingUtility;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SettingsDialogPane extends AnchorPane implements SpringInitializableNode, CloseableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsDialogPane.class);

    @FXML
    private ComboBox<Interval> intervalComboBox;
    @FXML
    private TextField snapshotIntervalValueTextField;
    @FXML
    private TextField graphLevelSpacingTextField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button applyButton;
    private final SettingRepository settingRepository;
    private final NodeUtility nodeUtility;
    private final TaskSchedulingService taskSchedulingService;
    private final SettingState settingState;
    private final ObjectProvider<ConfigurationChangeCheckTask> configurationChangeCheckTaskProvider;

    public SettingsDialogPane(@Value("classpath:/fxml/SettingsPane.fxml") Resource fxml,
                              SettingRepository settingRepository,
                              NodeUtility nodeUtility,
                              TaskSchedulingService taskSchedulingService,
                              SettingState settingState,
                              ObjectProvider<ConfigurationChangeCheckTask> configurationChangeCheckTaskProvider
    ) {
        this.settingRepository = settingRepository;
        this.nodeUtility = nodeUtility;
        this.taskSchedulingService = taskSchedulingService;
        this.settingState = settingState;
        this.configurationChangeCheckTaskProvider = configurationChangeCheckTaskProvider;
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        initIntervalCombobox();
        initTextFields();
        initApplyButton();
        cancelButton.setOnAction(this::close);
    }

    private void initApplyButton() {
        applyButton.prefWidthProperty().bind(cancelButton.widthProperty());
        applyButton.setOnAction(event -> {
            taskSchedulingService.cancelScheduledTask(ScheduledTaskType.CONFIGURATION_CHANGE_CHECK);
            if ( isValidNonZeroInt(snapshotIntervalValueTextField.getText())){
                try {
                    settingState.setSnapshotInterval(intervalComboBox.getValue(), Integer.valueOf(snapshotIntervalValueTextField.getText()));
                } catch (NumberFormatException e) {
                    LOGGER.warn("Invalid snapshot interval value: {}", snapshotIntervalValueTextField.getText(), e);
                }
            }
            Optional<Setting> optIntervalSetting = settingRepository.findBySettingName(SettingType.SNAPSHOT_INTERVAL.getName());
            optIntervalSetting.ifPresent(value -> {
                value.setStringValue(intervalComboBox.getValue()
                                             .displayValue());
                settingRepository.save(value);
            });
            Optional<Setting> optIntervalValue = settingRepository.findBySettingName(SettingType.SNAPSHOT_INTERVAL_VALUE.getName());
            optIntervalValue.ifPresent(value -> {
               value.setStringValue(snapshotIntervalValueTextField.getText());
               settingRepository.save(value);
            });
            taskSchedulingService.scheduleFixedRateTask(configurationChangeCheckTaskProvider.getIfAvailable(), ScheduledTaskType.CONFIGURATION_CHANGE_CHECK, settingState.getSnapshotIntervalInSeconds());
            LOGGER.info("Setting snapshot interval in seconds: {}", settingState.getSnapshotIntervalInSeconds());
            Optional<Setting> optLevelSetting = settingRepository.findBySettingName(SettingType.HIERARCHICAL_GRAPH_LEVEL_SPACING.getName());
            optLevelSetting.ifPresent(value -> {
                value.setStringValue(graphLevelSpacingTextField.getText());
                settingState.setGraphLevelSpacing(Integer.valueOf(graphLevelSpacingTextField.getText()));
                settingRepository.save(value);
            });
            this.close(event);
        });
    }

    private void initTextFields() {
        Optional<Setting>setting = settingRepository.findBySettingName(SettingType.SNAPSHOT_INTERVAL_VALUE.getName());
        setting.ifPresent(value -> snapshotIntervalValueTextField.setText(value.getStringValue()));

        setting = settingRepository.findBySettingName(SettingType.HIERARCHICAL_GRAPH_LEVEL_SPACING.getName());
        setting.ifPresent(value -> graphLevelSpacingTextField.setText(value.getStringValue()));
    }

    private void initIntervalCombobox() {
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
        intervalComboBox.setConverter(converter);
        intervalComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            ComboBoxResizingUtility.resizeToContent(intervalComboBox);
        });
        intervalComboBox.itemsProperty().addListener((observable, oldValue, newValue) -> {
            ComboBoxResizingUtility.resizeToContent(intervalComboBox);
        });
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
    }

    @Override
    public void close(ActionEvent event) {
        nodeUtility.closeNode(event);
    }

    private boolean isValidNonZeroInt(String s) {
        return s != null && s.matches("[1-9]\\d{0,2}");
    }
}
