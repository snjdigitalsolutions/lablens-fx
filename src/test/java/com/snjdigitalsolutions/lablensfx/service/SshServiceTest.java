package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.AbstractTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SshServiceTest extends AbstractTest {

    @Test
    @Order(1)
    void init() {
        //Arrange
        sshState.sshUsernameProperty()
                .setValue(username);
        sshState.passPhraseProperty()
                .setValue(passPhrase);
        sshState.passPhraseModeProperty()
                .setValue(PassPhraseMode.PROVIDED);

        //Act
        boolean success = sshService.init();

        //Assert
        assertTrue(success);
    }

    @Test
    @Order(2)
    void executeCommand() throws Exception {
        //Arrange

        //Act
        String response = sshService.executeCommand(testhost, 22, "whoami");

        //Assert
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    @Order(3)
    void shutdown() throws IOException {
        //Arrange

        //Act
        boolean success = sshService.shutdown();

        //Assert
        assertTrue(success);
    }
}
