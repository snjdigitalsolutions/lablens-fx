package com.snjdigitalsolutions.lablensfx.service.command.commandparser;

import com.snjdigitalsolutions.lablensfx.AbstractTest;
import com.snjdigitalsolutions.lablensfx.orm.FileSystemObject;
import com.snjdigitalsolutions.lablensfx.orm.model.FileSystemObjectModel;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListFileParserTest extends AbstractTest {

    @Test
    public void lineCountParsingTest() {
        //Arrange
        String content = contentReader.readContentFromPath(Path.of("src/test/resources/fileListing"));
        List<String> lines = Arrays.asList(content.split("\n"));
        if (lines.size() != 7){
            fail("Content line count is incorrect");
        }

        //Act
        List<FileSystemObjectModel> objects = listFileParser.getFileSystemObjectModels("/var/test", lines);

        //Assert
        assertFalse(objects.isEmpty());
        assertEquals(7, objects.size());

        FileSystemObjectModel twoExampleConf = objects.get(2);
        assertEquals(Instant.parse("2025-10-25T08:37:30.145187907Z"), twoExampleConf.getModifiedTime());
        assertEquals(644, Integer.parseInt(twoExampleConf.getPermission()));
        assertEquals("f", twoExampleConf.getFileType());
        assertEquals("two.example.com.conf", twoExampleConf.getFileName());
        assertEquals(1069L, twoExampleConf.getFileSize());
        assertEquals("/var/test", twoExampleConf.getParentPath());
    }

}
