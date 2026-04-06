package com.snjdigitalsolutions.lablensfx.utility;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class FilePathValidator {

    // Linux/macOS absolute paths: /etc/nginx/nginx.conf
    private final Pattern UNIX_ABSOLUTE =
            Pattern.compile("^(/[^/\0]+)+/?$");

    // Linux/macOS relative paths: etc/nginx or ./config/app.yml
    private final Pattern UNIX_RELATIVE =
            Pattern.compile("^(\\./|\\.\\./)?([^/\0]+/)*[^/\0]+$");

    // Windows absolute paths: C:\Users\foo or C:/Users/foo
    private final Pattern WINDOWS_ABSOLUTE =
            Pattern.compile("^[A-Za-z]:[/\\\\]([^<>:\"/\\\\|?*\0]+[/\\\\])*[^<>:\"/\\\\|?*\0]*$");

    // Docker Compose / config file extension whitelist (optional)
    private final Pattern CONFIG_FILE =
            Pattern.compile(".*\\.(conf|cfg|yml|yaml|toml|ini|env|json|xml)$",
                    Pattern.CASE_INSENSITIVE);

    public enum PathType { UNIX_ABSOLUTE, UNIX_RELATIVE, WINDOWS_ABSOLUTE, UNKNOWN }

    public PathType detect(String path) {
        if (path == null || path.isBlank()) return PathType.UNKNOWN;
        if (UNIX_ABSOLUTE.matcher(path).matches())    return PathType.UNIX_ABSOLUTE;
        if (WINDOWS_ABSOLUTE.matcher(path).matches()) return PathType.WINDOWS_ABSOLUTE;
        if (UNIX_RELATIVE.matcher(path).matches())    return PathType.UNIX_RELATIVE;
        return PathType.UNKNOWN;
    }

    public boolean isValid(String path) {
        return detect(path) != PathType.UNKNOWN;
    }

}
