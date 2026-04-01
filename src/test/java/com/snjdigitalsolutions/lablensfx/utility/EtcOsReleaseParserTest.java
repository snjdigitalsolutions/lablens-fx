package com.snjdigitalsolutions.lablensfx.utility;

import com.snjdigitalsolutions.lablensfx.AbstractTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EtcOsReleaseParserTest extends AbstractTest {

    @Test
    @Order(1)
    void parseOsRelease() {
        //Arrange
        int expectedResult = 19;
        String content = contentReader.readContentFromPath(Path.of("src/test/resources/osReleaseSample"));

        //Act
        Map<String, String> releaseValues = etcOsReleaseParser.parseOsRelease(content);

        //Assert
        assertEquals(expectedResult, releaseValues.size());
    }

    @Test
    @Order(2)
    void getPrettyName() {
        //Arrange
        String expectedResult = "Rocky Linux 9.7 (Blue Onyx)";
        String content = contentReader.readContentFromPath(Path.of("src/test/resources/osReleaseSample"));

        //Act
        String prettyName = etcOsReleaseParser.getPrettyName(content);

        //Assert
        assertEquals(expectedResult, prettyName);

    }
}
