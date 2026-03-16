package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.lablensfx.nodes.HostPane;
import com.snjdigitalsolutions.springbootutilityfx.application.AbstractStageReadyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class LabLensFxBootReadyListener extends AbstractStageReadyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabLensFxBootReadyListener.class);
    private final LabLensFxBootReadyController labLensFxBootReadyController;
    private final HostPane hostPane;

    public LabLensFxBootReadyListener(@Value("classpath:/fxml/RootPane.fxml") Resource fxml,
                                      LabLensFxBootReadyController labLensFxBootReadyController, HostPane hostPane){
        super(fxml);
        this.labLensFxBootReadyController = labLensFxBootReadyController;
        this.hostPane = hostPane;
    }
}
