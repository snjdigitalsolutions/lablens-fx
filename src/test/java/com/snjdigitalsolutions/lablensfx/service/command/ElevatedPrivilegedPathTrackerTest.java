package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.AbstractTest;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ElevatedPrivilegedPathTrackerTest extends AbstractTest {

    @Mock
    private ComputeResource computeResource;

    @Test
    @Order(1)
    void hasBeenCheckedFalseTest() {
        //Arrange
        setSshProperties();
        sshService.init();
        when(computeResource.getHostName()).thenReturn(testhost);
        when(computeResource.getSshPort()).thenReturn(22);

        //Act
        boolean checked = elevatedPrivilegedPathTracker.hasBeenChecked(computeResource,"/var/log");

        //Assert
        assertFalse(checked);
    }

    @Test
    @Order(2)
    void checkElevationRequiredTest() throws Exception {
        //Arrange
        setSshProperties();
        sshService.init();
        when(computeResource.getHostName()).thenReturn(testhost);
        when(computeResource.getSshPort()).thenReturn(22);

        //Act
        boolean required = false;
        if (!elevatedPrivilegedPathTracker.hasBeenChecked(computeResource, "/root")){
            required = elevatedPrivilegedPathTracker.checkElevationRequired(computeResource, "/root");
        }

        //Assert
        assertTrue(required);
    }

    @Test
    @Order(3)
    void isElevationRequiredTest() throws Exception {
        //Arrange
        setSshProperties();
        sshService.init();
        when(computeResource.getHostName()).thenReturn(testhost);
        when(computeResource.getSshPort()).thenReturn(22);

        //Act
        if (!elevatedPrivilegedPathTracker.hasBeenChecked(computeResource, "/root")){
            elevatedPrivilegedPathTracker.checkElevationRequired(computeResource, "/root");
        }
        boolean required = elevatedPrivilegedPathTracker.isElevationRequired(computeResource,"/root");

        //Assert
        assertTrue(required);
    }
}
