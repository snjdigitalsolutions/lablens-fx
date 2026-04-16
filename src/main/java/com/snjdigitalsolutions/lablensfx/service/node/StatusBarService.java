package com.snjdigitalsolutions.lablensfx.service.node;

import com.snjdigitalsolutions.lablensfx.state.ApplicationState;
import com.snjdigitalsolutions.lablensfx.state.ApplicationView;
import com.snjdigitalsolutions.lablensfx.state.StatusBarState;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import javafx.application.Platform;
import javafx.scene.Cursor;
import org.controlsfx.control.StatusBar;
import org.springframework.stereotype.Service;

@Service
public class StatusBarService implements SpringInitializableNode {

    private StatusBar statusBar;
    private final StatusBarState statusBarState;
    private final ApplicationState applicationState;

    public StatusBarService(StatusBarState statusBarState,
                            ApplicationState applicationState
    ) {
        this.statusBarState = statusBarState;
        this.applicationState = applicationState;
    }

    @Override
    public void performIntialization() {
        statusBar.setText("");
        statusBarState.numberOfSelectedHostsProperty().addListener((obj, oldVal, newVal) -> {
            if (statusBarState.getSelectedApplicationView()
                    .equals(ApplicationView.DASHBOARD) && newVal.intValue() > 0) {
                statusBar.setText("Hosts Selected: " + newVal);
                statusBarState.disableDeleteHostMenuItemProperty().setValue(false);
            } else if (statusBarState.getSelectedApplicationView()
                    .equals(ApplicationView.DASHBOARD) && newVal.intValue() == 0) {
                statusBar.setText("");
                statusBarState.disableDeleteHostMenuItemProperty().setValue(true);
            }
        });
    }

    public void setStatusbar(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    public void setStatusText(String text) {
        statusBar.setText(text);
    }

    public void addLoadingFilesMessage() {
        applicationState.loadingDataProperty().setValue(true);
        statusBar.setText(statusBar.getText() + " -- Loading files");
    }

    public void removeLoadingFilesMessage() {
        applicationState.loadingDataProperty().setValue(false);
        statusBar.setText(statusBar.getText().replace(" -- Loading files", ""));
    }

    public void setSelectedHostCount(int numberOfSelectedHosts) {
        statusBarState.numberOfSelectedHostsProperty().setValue(numberOfSelectedHosts);
    }
}
