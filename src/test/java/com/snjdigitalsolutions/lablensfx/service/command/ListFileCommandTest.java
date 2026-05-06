package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.AbstractCommandTest;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import org.junit.jupiter.api.*;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ListFileCommandTest extends AbstractCommandTest {

    @Mock
    private ComputeResource computeResource;

    @Nested
    class WhenSshNotInitialized {

        @Test
        @Order(1)
        void executeCommand() {
            //Arrange
            when(computeResource.getHostName()).thenReturn(testhost);
            when(computeResource.getIpAddress()).thenReturn(ipAddress);
            when(computeResource.getSshPort()).thenReturn(22);

            //Act
            Exception ex = assertThrows(Exception.class, () -> listFileCommand.executeCommand(computeResource, ""));

            //Assert
            assertThat(ex.getMessage()).contains("File path cannot be blank");
        }
    }

    @Test
    @Order(2)
    void listFiles() throws Exception {
        //Arrange
        setSshProperties();
        sshService.init();
        when(computeResource.getHostName()).thenReturn(testhost);
        when(computeResource.getIpAddress()).thenReturn(ipAddress);
        when(computeResource.getSshPort()).thenReturn(22);

        //Act
        List<String> files = listFileCommand.performCommand(computeResource, "/etc/nginx/conf.d", false);

        //Assert
        assertFalse(files.isEmpty());

    }
}
