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
    private Path sshDir = Paths.get(System.getProperty("user.home"), ".ssh");

    @Override
    public void performIntialization() {
        privateKeyFileNameList.addAll(Arrays.asList("id_rsa",
                "id_ecdsa",
                "id_ecdsa_sk",
                "id_ed25519",
                "id_ed25519_sk",
                "id_dsa"));
    }

    public List<KeyPair> getAvailableKeys() {
        for (String fileName : privateKeyFileNameList){
            Path filePath = sshDir.resolve(fileName);
            if (Files.isRegularFile(filePath)){

            }
        }
        return null;
    }
}
