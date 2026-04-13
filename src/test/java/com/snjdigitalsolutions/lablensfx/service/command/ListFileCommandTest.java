package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.AbstractTest;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ListFileCommandTest extends AbstractTest {

    @Mock
    private ComputeResource computeResource;

    @Test
    @Order(1)
    void executeCommand() {
        //Arrange
        when(computeResource.getHostName()).thenReturn(testhost);
        when(computeResource.getSshPort()).thenReturn(22);

        //Act
        Exception ex = assertThrows(Exception.class, () -> listFileCommand.executeCommand(computeResource, ""));

        //Assert
        assertThat(ex.getMessage()).contains("File path cannot be blank");
    }

    @Test
    @Order(2)
    void listFiles() throws Exception {
        //Arrange
        setSshProperties();
        sshService.init();
        when(computeResource.getHostName()).thenReturn(testhost);
        when(computeResource.getSshPort()).thenReturn(22);

        //Act
        List<String> files = listFileCommand.listFiles(computeResource, "/etc/nginx/conf.d");

        //Assert
        assertFalse(files.isEmpty());

    }
}
