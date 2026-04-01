package com.snjdigitalsolutions.lablensfx;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LablensFxBootTests {

    @BeforeAll
    static void initToolkit() {
        Platform.startup(() -> {

        });
    }

    @Test
    void contextLoads() {
    }

}
