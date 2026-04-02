package com.snjdigitalsolutions.lablensfx.utility;

import com.snjdigitalsolutions.lablensfx.AbstractTest;
import com.snjdigitalsolutions.lablensfx.properties.SshProperties;
import com.snjdigitalsolutions.lablensfx.service.PassPhraseMode;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SshKeyLoaderTest extends AbstractTest {

    private final SshProperties sshProperties = new SshProperties();

    @Test
    public void getAvailableKeysTest() {
        //Arrange
        Path directoryPath = keyDirectoryProvider.keyDirectoryPath();

        //Act
        List<Path> filePaths = sshKeyLoader.getAvailableKeyFilePaths();

        //Assert
        assertEquals("src/test/resources", directoryPath.toString());
        assertFalse(filePaths.isEmpty());


    }

}
