package com.snjdigitalsolutions.lablensfx.utility;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Profile(("!test"))
public class HomeDirectoryKeyPathProvider implements KeyDirectoryProvider {

    @Override
    public Path keyDirectoryPath() {
        return Paths.get(System.getProperty("user.home"), ".ssh");
    }
}
