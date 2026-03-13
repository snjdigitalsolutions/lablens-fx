package com.snjdigitalsolutions.lablensfx;

import com.snjdigitalsolutions.springbootutilityfx.application.AbstractFXApplication;

public class LabLensFX extends AbstractFXApplication {

    @Override
    public void init() throws Exception {
        initialize(createInitializer(this), LablensFxBoot.class);
    }

}
