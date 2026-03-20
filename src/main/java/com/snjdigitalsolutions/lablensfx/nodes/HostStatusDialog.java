package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class HostStatusDialog extends AnchorPane implements SpringInitializableNode {

    @FXML
    @Getter
    private ProgressBar statusCheckProgressBar;
    private Runnable onDialogClosed;

    public HostStatusDialog(@Value("classpath:/fxml/HostStatusDialog.fxml") Resource fxml) {
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((wObs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> {
                            if (onDialogClosed != null) {
                                onDialogClosed.run();
                            }
                        });
                    }
                });
            }
        });
    }

    public void closeDialog() {
        ((Stage) sceneProperty().get().getWindow()).close();
    }

    public void setOnDialogClosed(Runnable onDialogClosed) {
        this.onDialogClosed = onDialogClosed;
    }
}
