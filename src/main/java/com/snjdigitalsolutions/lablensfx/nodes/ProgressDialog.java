package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ProgressDialog extends AnchorPane implements SpringInitializableNode {

    @FXML
    private Label progressLabel;
    @FXML
    @Getter
    private ProgressBar progressBar;
    @Setter
    private Runnable onDialogClosed;

    /**
     * Creates a progress dialog backed by the given FXML resource.
     *
     * @param fxml the FXML resource for the progress dialog layout
     */
    public ProgressDialog(@Value("classpath:/fxml/ProgressDialog.fxml") Resource fxml) {
        NodeLoader.load(fxml, this);
    }

    /**
     * Initializes the dialog layout and registers a close-request handler.
     */
    @Override
    public void performIntialization() {
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((wObs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
                            if (onDialogClosed != null) {
                                onDialogClosed.run();
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Closes and hides the progress dialog.
     */
    public void closeDialog() {
        if (sceneProperty().get() != null){
            Window window = sceneProperty().get().getWindow();
            if (window != null){
                ((Stage)window).close();
            }
        }
    }

    /**
     * Updates the progress message label.
     *
     * @param text the message to display
     */
    public void setProgressText(String text){
        this.progressLabel.setText(text);
    }
}
