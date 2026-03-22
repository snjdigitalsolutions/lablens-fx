package com.snjdigitalsolutions.lablensfx.utility;

import com.snjdigitalsolutions.lablensfx.properties.SshProperties;
import com.snjdigitalsolutions.lablensfx.service.PassPhraseMode;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class SshKeyLoaderTest {


    private SshProperties sshProperties = new SshProperties();

    @Test
    public void getAvailableKeysTest() {
        //Arrange
        SshKeyLoader sshKeyLoader = new SshKeyLoader(sshProperties);
        sshKeyLoader.performIntialization();
        sshProperties.passPhraseProperty().setValue("");
        sshProperties.passPhraseModeProperty().setValue(PassPhraseMode.PROVIDED);

        //Act
        List<KeyPair> keyPairs = sshKeyLoader.getAvailableKeys();
        keyPairs.forEach(pair -> {
            System.out.println(pair.toString());
        });

        //Assert
        assertNotNull(keyPairs);
    }

}
