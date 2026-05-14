package com.snjdigitalsolutions.lablensfx.utility;

import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Getter
public class SshKeyLoader implements SpringInitializableNode {

    private final List<String> privateKeyFileNameList = new ArrayList<>();
    private final KeyDirectoryProvider keyDirectoryProvider;
    private boolean initialized = false;

    /**
     * Creates the key loader with the provider that resolves the SSH key directory.
     *
     * @param keyDirectoryProvider supplies the path to the directory containing key files
     */
    public SshKeyLoader(KeyDirectoryProvider keyDirectoryProvider) {
        this.keyDirectoryProvider = keyDirectoryProvider;
    }

    /**
     * Populates the list of known private key file names and marks this loader as initialized.
     */
    @Override
    public void performIntialization() {
        privateKeyFileNameList.addAll(Arrays.asList("id_rsa",
                "id_ecdsa",
                "id_ecdsa_sk",
                "id_ed25519",
                "id_ed25519_sk",
                "id_dsa"));
        initialized = true;
    }

    /**
     * Returns the list of paths to SSH private key files found in the key directory.
     *
     * @return list of available SSH key file paths; empty if none are found
     */
    public List<Path> getAvailableKeyFilePaths() {
        List<Path> validFilePaths = new ArrayList<>();
        if (!initialized){
            performIntialization();
        }
        for (String fileName : privateKeyFileNameList){
            Path filePath = Path.of(keyDirectoryProvider.keyDirectoryPath().toString(), fileName);
            if (Files.isRegularFile(filePath)){
                validFilePaths.add(filePath);
            }
        }
        return validFilePaths;
    }
}
