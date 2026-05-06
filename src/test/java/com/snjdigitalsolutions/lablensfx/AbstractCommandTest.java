package com.snjdigitalsolutions.lablensfx;

import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractCommandTest extends AbstractTest {

    @BeforeEach
    void setUp() {
        setSshProperties();
        sshService.init();
    }
}
