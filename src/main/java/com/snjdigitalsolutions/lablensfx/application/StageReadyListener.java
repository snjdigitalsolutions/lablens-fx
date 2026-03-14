package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.lablensfx.nodes.HostPane;
import com.snjdigitalsolutions.springbootutilityfx.application.AbstractStageReadyListener;
import com.snjdigitalsolutions.springbootutilityfx.event.StageReadyEvent;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
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
    }
}
