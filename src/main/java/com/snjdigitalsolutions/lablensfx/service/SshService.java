package com.snjdigitalsolutions.lablensfx.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

@Service
public class SshService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshService.class);

    private SshClient client;

    @PostConstruct
    public void init() {
        client = SshClient.setUpDefaultClient();

        // Accept the host key on first connect; swap for a
        // KnownHostsServerKeyVerifier in production if you want strict checking
        client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);

        Path sshDir = Paths.get(System.getProperty("user.home"), ".ssh");
        client.setKeyIdentityProvider(
                new FileKeyPairProvider(sshDir)
        );

        client.start();
        LOGGER.info("SSH client started, loading keys from {}", sshDir);
    }

    @PreDestroy
    public void shutdown() throws IOException {
        client.stop();
    }

    /**
     * Opens a session to the given host and runs a command, returning its output.
     * Closes the session when done — call this per-command for simple use cases.
     */
    public String executeCommand(String host, int port, String username, String command)
            throws Exception {

        try (ClientSession session = client.connect(username, host, port)
                .verify(10, TimeUnit.SECONDS)
                .getSession()) {

            session.auth().verify(10, TimeUnit.SECONDS);

            try (ByteArrayOutputStream stdout = new ByteArrayOutputStream();
                 ByteArrayOutputStream stderr = new ByteArrayOutputStream();
                 ChannelExec channel = session.createExecChannel(command)) {

                channel.setOut(stdout);
                channel.setErr(stderr);
                channel.open().verify(10, TimeUnit.SECONDS);

                // Wait for the command to finish (or timeout after 30 s)
                channel.waitFor(
                        EnumSet.of(ClientChannelEvent.CLOSED),
                        TimeUnit.SECONDS.toMillis(30)
                );

                if (stderr.size() > 0) {
                    LOGGER.warn("stderr from [{}]: {}", command, stderr);
                }

                return stdout.toString(StandardCharsets.UTF_8);
            }
        }
    }
}
