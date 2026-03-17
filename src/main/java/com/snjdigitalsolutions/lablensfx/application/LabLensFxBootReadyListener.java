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

    public LabLensFxBootReadyListener(@Value("classpath:/fxml/RootPane.fxml") Resource fxml, LabLensFxPostShowAction labLensFxPostShowAction) {
        super(fxml);
        this.labLensFxPostShowAction = labLensFxPostShowAction;
    }

    @Override
    public void setPostShowRunnable() {
        LOGGER.debug("Setting post show runnable");
        SplashController.setPostShowRunnable(labLensFxPostShowAction);
    }
}
