package com.snjdigitalsolutions.lablensfx.utility;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EtcOsReleaseParser {

    /**
     * Parses the contents of a Linux {@code /etc/os-release} file into a key-value map.
     *
     * @param content the full text content of the {@code /etc/os-release} file
     * @return a map of key-value pairs extracted from the file
     */
    public Map<String, String> parseOsRelease(String content) {
        return Arrays.stream(content.split("\n"))
                .filter(line -> line.contains("=") && !line.startsWith("#"))
                .collect(Collectors.toMap(
                        line -> line.substring(0, line.indexOf('=')).trim(),
                        line -> line.substring(line.indexOf('=') + 1)
                                .trim()
                                .replaceAll("^\"|\"$", "") // strip surrounding quotes
                ));
    }

    /**
     * Extracts the {@code PRETTY_NAME} value from {@code /etc/os-release} content.
     *
     * @param content the full text content of the {@code /etc/os-release} file
     * @return the pretty OS name, or an empty string if the field is absent
     */
    public String getPrettyName(String content) {
        String prettyName = "";
        Map<String, String> keyValue = parseOsRelease(content);
        if (keyValue.containsKey("PRETTY_NAME")) {
            prettyName = keyValue.get("PRETTY_NAME");
        }
        return prettyName;
    }

}
