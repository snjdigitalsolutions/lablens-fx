package com.snjdigitalsolutions.lablensfx.utility;

import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
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

    /**
     * Detects the type of the given path string.
     *
     * @param path the path string to inspect
     * @return one of {@code UNIX_ABSOLUTE}, {@code UNIX_RELATIVE}, {@code WINDOWS_ABSOLUTE}, or {@code UNKNOWN}
     */
    public PathType detect(String path) {
        if (path == null || path.isBlank()) return PathType.UNKNOWN;
        if (UNIX_ABSOLUTE.matcher(path).matches())    return PathType.UNIX_ABSOLUTE;
        if (WINDOWS_ABSOLUTE.matcher(path).matches()) return PathType.WINDOWS_ABSOLUTE;
        if (UNIX_RELATIVE.matcher(path).matches())    return PathType.UNIX_RELATIVE;
        return PathType.UNKNOWN;
    }

    /**
     * Returns whether the given path is a recognized valid path type.
     *
     * @param path the path string to validate
     * @return {@code true} if the path is valid; {@code false} if it is {@code UNKNOWN}
     */
    public boolean isValid(String path) {
        return detect(path) != PathType.UNKNOWN;
    }

    /**
     * Joins all segments into a single path string and returns it if valid.
     *
     * @param segments one or more path segments to join and validate
     * @return an {@link Optional} containing the joined path if valid, or empty if not
     */
    public Optional<String> allValid(String... segments) {
        if (segments == null || segments.length == 0) return Optional.empty();
        String joined = Paths.get(segments[0], Arrays.copyOfRange(segments, 1, segments.length)).toString();
        return isValid(joined) ? Optional.of(joined) : Optional.empty();
    }

}
