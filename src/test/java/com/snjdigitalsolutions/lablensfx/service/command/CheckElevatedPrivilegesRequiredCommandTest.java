package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.AbstractCommandTest;
import com.snjdigitalsolutions.lablensfx.AbstractTest;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import org.junit.jupiter.api.*;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CheckElevatedPrivilegesRequiredCommandTest extends AbstractCommandTest {

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
            Exception ex = assertThrows(Exception.class, () -> checkElevatedPrivilegesRequiredCommand.performCommand(computeResource, ""));

            //Assert
            assertThat(ex.getMessage()).contains("File path cannot be blank");
        }
    }

    @Test
    @Order(2)
    void checkFilePathElevationNotRequired() throws Exception {
        //Arrange
        when(computeResource.getHostName()).thenReturn(testhost);
        when(computeResource.getIpAddress()).thenReturn(ipAddress);
        when(computeResource.getSshPort()).thenReturn(22);

        //Act
        boolean needed = checkElevatedPrivilegesRequiredCommand.performCommand(computeResource, "/var/log");

        //Assert
        assertFalse(needed);
    }

    @Test
    void checkFilePathElevationRequired() throws Exception {
        //Arrange
        when(computeResource.getHostName()).thenReturn(testhost);
        when(computeResource.getIpAddress()).thenReturn(ipAddress);
        when(computeResource.getSshPort()).thenReturn(22);

        //Act
        boolean needed = checkElevatedPrivilegesRequiredCommand.performCommand(computeResource, "/root");

        //Assert
        assertTrue(needed);
    }
}
