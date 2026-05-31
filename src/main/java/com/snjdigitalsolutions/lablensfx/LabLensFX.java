package com.snjdigitalsolutions.lablensfx;

import com.snjdigitalsolutions.springbootutilityfx.application.AbstractFXApplication;

public class LabLensFX extends AbstractFXApplication {

    /**
     * Initializes the JavaFX application context and wires the primary Spring-managed stage.
     */
    @Override
    public void init() throws Exception {
        initialize(createInitializer(this), LablensFxBoot.class);
    }

}
