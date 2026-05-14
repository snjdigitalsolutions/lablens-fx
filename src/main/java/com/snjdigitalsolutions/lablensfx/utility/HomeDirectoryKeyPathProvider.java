package com.snjdigitalsolutions.lablensfx.utility;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Profile(("!test"))
public class HomeDirectoryKeyPathProvider implements KeyDirectoryProvider {

    /**
     * Returns the path to the {@code .ssh} directory in the current user's home directory.
     *
     * @return the absolute path to the SSH key directory
     */
    @Override
    public Path keyDirectoryPath() {
        return Paths.get(System.getProperty("user.home"), ".ssh");
    }
}
