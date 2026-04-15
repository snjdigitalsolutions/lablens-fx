package com.snjdigitalsolutions.lablensfx.service.node;

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

    public StatusBarService(StatusBarState statusBarState) {
        this.statusBarState = statusBarState;
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
        Platform.runLater(() -> {
            statusBar.setText(statusBar.getText() + " -- Loading files");
            if (statusBar.getScene() != null) {
                statusBar.getScene().getRoot().setCursor(Cursor.WAIT);
            }
        });
    }

    public void removeLoadingFilesMessage() {
        Platform.runLater(() -> {
            statusBar.setText(statusBar.getText().replace(" -- Loading files", ""));
            if (statusBar.getScene() != null) {
                statusBar.getScene().getRoot().setCursor(Cursor.DEFAULT);
            }
        });
    }

    public void setSelectedHostCount(int numberOfSelectedHosts) {
        statusBarState.numberOfSelectedHostsProperty().setValue(numberOfSelectedHosts);
    }
}
