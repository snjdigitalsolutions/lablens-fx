package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.springbootutilityfx.application.AbstractStageReadyListener;
import com.snjdigitalsolutions.springbootutilityfx.splash.SplashController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class LabLensFxBootReadyListener extends AbstractStageReadyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabLensFxBootReadyListener.class);
    private final LabLensFxPostShowAction labLensFxPostShowAction;

    /**
     * Creates a listener that fires a post-show action when the primary stage becomes visible.
     *
     * @param fxml the FXML resource for the primary stage
     * @param labLensFxPostShowAction the action to execute after the stage is shown
     */
    public LabLensFxBootReadyListener(@Value("classpath:/fxml/RootPane.fxml") Resource fxml, LabLensFxPostShowAction labLensFxPostShowAction) {
        super(fxml);
        this.labLensFxPostShowAction = labLensFxPostShowAction;
    }

    /**
     * Registers the post-show runnable on the primary stage's shown event.
     */
    @Override
    public void setPostShowRunnable() {
        LOGGER.debug("Setting post show runnable");
        SplashController.setPostShowRunnable(labLensFxPostShowAction);
    }
}
