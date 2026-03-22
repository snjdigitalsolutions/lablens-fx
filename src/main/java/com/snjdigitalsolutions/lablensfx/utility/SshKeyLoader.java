package com.snjdigitalsolutions.lablensfx.utility;

import com.snjdigitalsolutions.lablensfx.properties.SshProperties;
import com.snjdigitalsolutions.lablensfx.service.PassPhraseMode;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.keyprovider.KeyIdentityProvider;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SshKeyLoader implements SpringInitializableNode {

    private final SshProperties sshProperties;
    private final List<String> privateKeyFileNameList = new ArrayList<>();
    private final Path sshDir = Paths.get(System.getProperty("user.home"), ".ssh");

    public SshKeyLoader(SshProperties sshProperties) {
        this.sshProperties = sshProperties;
    }

    @Override
    public void performIntialization() {
        privateKeyFileNameList.addAll(Arrays.asList("id_rsa",
                "id_ecdsa",
                "id_ecdsa_sk",
                "id_ed25519",
                "id_ed25519_sk",
                "id_dsa"));
    }

    public KeyIdentityProvider getKeyIdentityProvider() {
        return KeyIdentityProvider.wrapKeyPairs(getAvailableKeys());
    }

    public List<KeyPair> getAvailableKeys() {
        List<KeyPair> keyPairList = new ArrayList<>();
        try {
            FilePasswordProvider passwordProvider = null;
            if (sshProperties.getPassPhraseMode().equals(PassPhraseMode.PROVIDED)){
                passwordProvider = FilePasswordProvider.of(sshProperties.getPassPhrase());
            }
            for (String fileName : privateKeyFileNameList){
                Path filePath = sshDir.resolve(fileName);
                if (Files.isRegularFile(filePath)){
                    Iterable<KeyPair> keyPairs = SecurityUtils.loadKeyPairIdentities(null, filePath::toString, Files.newInputStream(filePath), passwordProvider);
                    if (keyPairs != null){
                        keyPairs.forEach(keyPairList::add);
                    }
                }
            }
        } catch (IOException | GeneralSecurityException e) {
            //TODO show application message to user and try to resolve
            throw new RuntimeException(e);
        }
        return keyPairList;
    }
}
