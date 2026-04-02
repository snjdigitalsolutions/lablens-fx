package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.properties.SshProperties;
import com.snjdigitalsolutions.lablensfx.utility.SshKeyLoader;
import jakarta.annotation.PreDestroy;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SshService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshService.class);
    private final SshProperties sshProperties;
    private final SshKeyLoader sshKeyLoader;
    private SshClient client;
    private boolean clientInitialized = false;

    public SshService(SshProperties sshProperties, SshKeyLoader sshKeyLoader) {
        this.sshProperties = sshProperties;
        this.sshKeyLoader = sshKeyLoader;
    }

    synchronized public boolean init() {
        if (!clientInitialized) {
            client = SshClient.setUpDefaultClient();
            client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
            List<Path> availableKeyPaths = sshKeyLoader.getAvailableKeyFilePaths();
            if (availableKeyPaths.isEmpty()) {
                LOGGER.error("No key files available");
            } else {
                Path sshKeyPath = availableKeyPaths.getFirst();
                FileKeyPairProvider keyPairProvider = new FileKeyPairProvider(sshKeyPath);
                if (sshProperties.getPassPhraseMode()
                        .equals(PassPhraseMode.PROVIDED)) {
                    keyPairProvider.setPasswordFinder(FilePasswordProvider.of(sshProperties.getPassPhrase()));
                    client.setKeyIdentityProvider(keyPairProvider);
                    client.start();
                    LOGGER.info("SSH client started, loading keys from {}", sshKeyPath);
                    clientInitialized = true;
                } else {
                    LOGGER.error("Passphrase has not been set");
                }
            }
        }
        return clientInitialized;
    }

    @PreDestroy
    public boolean shutdown() throws IOException {
        boolean success = false;
        if (client != null) {
            client.stop();
            success = true;
        }
        return success;
    }

    /**
     * Opens a session to the given host and runs a command, returning its output.
     * Closes the session when done — call this per-command for simple use cases.
     */
    public String executeCommand(String host, int port, String command) throws Exception {
        String response = "";
        if (clientInitialized){
            try (ClientSession session = client.connect(sshProperties.getSshUsername(), host, port)
                    .verify(10, TimeUnit.SECONDS)
                    .getSession()) {

                session.auth()
                        .verify(10, TimeUnit.SECONDS);

                try (ByteArrayOutputStream stdout = new ByteArrayOutputStream(); ByteArrayOutputStream stderr = new ByteArrayOutputStream(); ChannelExec channel = session.createExecChannel(command)) {
                    channel.setOut(stdout);
                    channel.setErr(stderr);
                    channel.open()
                            .verify(10, TimeUnit.SECONDS);

                    // Wait for the command to finish (or timeout after 30 s)
                    channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(30));

                    if (stderr.size() > 0) {
                        LOGGER.warn("stderr from [{}]: {}", command, stderr);
                    }

                    response = stdout.toString(StandardCharsets.UTF_8);
                }
            }
        } else {
            LOGGER.error("Client not initialized");
        }
       return response;
    }
}
