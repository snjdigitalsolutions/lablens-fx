package com.snjdigitalsolutions.lablensfx;

import com.snjdigitalsolutions.lablensfx.utility.KeyDirectoryProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@Profile("test")
public class TestKeyPathProvider implements KeyDirectoryProvider {

    @Override
    public Path keyDirectoryPath() {
        return Path.of("src/test/resources");
    }
}
