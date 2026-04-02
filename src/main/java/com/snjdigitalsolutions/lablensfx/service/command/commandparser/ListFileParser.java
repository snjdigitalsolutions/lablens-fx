package com.snjdigitalsolutions.lablensfx.service.command.commandparser;

import com.snjdigitalsolutions.lablensfx.orm.FileSystemObject;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ListFileParser {

    public List<FileSystemObject> getFileSystemObjects(String parentPath, List<String> listFileResult){
        List<FileSystemObject> fileSystemObjects = new ArrayList<>();
        listFileResult.forEach(line -> {
            String[] elements = line.split(" ");
            FileSystemObject fileObj = new FileSystemObject();
            fileObj.setModifiedTime(parseModifiedTime(elements[0]));
            fileObj.setPermission(Integer.parseInt(elements[1]));
            fileObj.setFileType(elements[2]);
            fileObj.setFileName(elements[3]);
            fileObj.setFileSize(Long.parseLong(elements[4]));
            fileObj.setParentPath(parentPath);
            fileSystemObjects.add(fileObj);
        });
        return fileSystemObjects;
    }

    private Instant parseModifiedTime(String value) {
        // Replace '+' date/time separator with 'T' for ISO-8601 compatibility
        String normalized = value.replace("+", "T");

        // Truncate fractional seconds to 9 digits (nanosecond precision max for Instant)
        int dotIndex = normalized.indexOf('.');
        if (dotIndex != -1 && (normalized.length() - dotIndex - 1) > 9) {
            normalized = normalized.substring(0, dotIndex + 10);
        }

        LocalDateTime localDateTime = LocalDateTime.parse(normalized, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

}
