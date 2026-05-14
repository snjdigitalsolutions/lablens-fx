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

    /**
     * Creates the status bar service with required state dependencies.
     */
    public StatusBarService(StatusBarState statusBarState,
                            ApplicationState applicationState
    ) {
        this.statusBarState = statusBarState;
        this.applicationState = applicationState;
    }

    /**
     * Binds the status bar to the backing state properties after Spring initialization.
     */
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

    /**
     * Replaces the managed status bar instance.
     *
     * @param statusBar the new status bar component
     */
    public void setStatusbar(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    /**
     * Updates the primary status bar text.
     *
     * @param text the message to display
     */
    public void setStatusText(String text) {
        statusBar.setText(text);
    }

    /**
     * Adds a "Loading files" suffix to the status bar text and sets the loading state.
     */
    public void addLoadingFilesMessage() {
        applicationState.loadingDataProperty().setValue(true);
        statusBar.setText(statusBar.getText() + " -- Loading files");
    }

    /**
     * Removes the "Loading files" suffix from the status bar text and clears the loading state.
     */
    public void removeLoadingFilesMessage() {
        applicationState.loadingDataProperty().setValue(false);
        statusBar.setText(statusBar.getText().replace(" -- Loading files", ""));
    }

    /**
     * Updates the selected-host count displayed in the status bar.
     *
     * @param numberOfSelectedHosts the number of currently selected hosts
     */
    public void setSelectedHostCount(int numberOfSelectedHosts) {
        statusBarState.numberOfSelectedHostsProperty().setValue(numberOfSelectedHosts);
    }
}
