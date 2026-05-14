package com.snjdigitalsolutions.lablensfx.utility;

import java.nio.file.Path;

public interface KeyDirectoryProvider {

    /**
     * Returns the path to the directory containing SSH key files.
     *
     * @return the SSH key directory path
     */
    Path keyDirectoryPath();

}
