package com.snjdigitalsolutions.lablensfx;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;

@Component
@Profile("test")
public class ContentReader {

    public String readContentFromPath(Path contentPath) {
        StringBuilder contentBuilder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(contentPath.toFile()))){
            String inLine = reader.readLine();
            while (inLine != null){
                contentBuilder.append(inLine).append("\n");
                inLine = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return contentBuilder.toString();
    }

}
