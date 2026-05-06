package com.snjdigitalsolutions.lablensfx.service.command;

import com.snjdigitalsolutions.lablensfx.AbstractCommandTest;
import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class MD5SumCommandTest extends AbstractCommandTest {

    @Mock
    private ComputeResource computeResource;

    @Test
    void testMd5Sum() throws Exception {
        //Arrange
        when(computeResource.getHostName()).thenReturn(testhost);
        when(computeResource.getIpAddress()).thenReturn(ipAddress);
        when(computeResource.getSshPort()).thenReturn(22);
        String filePath = "/etc/nginx/conf.d/argocd.snjdigitalsolutions.com.conf";
        String expectedResult = "189898b604a962e31a5d1a034d096947";

        //Act
        String md5Result = md5SumCommand.performCommand(computeResource, filePath, true);

        //Assert
        assertNotNull(md5Result);
        assertEquals(expectedResult, md5Result);
    }

}
