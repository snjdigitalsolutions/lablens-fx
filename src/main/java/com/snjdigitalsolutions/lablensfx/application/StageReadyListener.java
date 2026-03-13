package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.lablensfx.nodes.HostPane;
import com.snjdigitalsolutions.springbootutilityfx.application.AbstractStageReadyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class StageReadyListener extends AbstractStageReadyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StageReadyListener.class);
    private final StageReadyController stageReadyController;
    private final HostPane hostPane;

    public StageReadyListener(@Value("classpath:/fxml/RootPane.fxml") Resource fxml,
                              StageReadyController stageReadyController, HostPane hostPane){
        super(fxml);
        this.stageReadyController = stageReadyController;
        this.hostPane = hostPane;
    }

    @Override
    public void performIntialization() {
        LOGGER.debug("Initializing...");
        stageReadyController.addHostPane();
        stageReadyController.performIntialization();
    }
}
