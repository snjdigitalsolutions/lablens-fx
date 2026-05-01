package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.state.SshState;
import com.snjdigitalsolutions.lablensfx.utility.SshKeyLoader;
import jakarta.annotation.PreDestroy;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.scp.client.ScpClient;
import org.apache.sshd.scp.client.ScpClientCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
public class SshService {

    @Value("${application.ssh.sudokey}")
    private String sudoKey;
    private static final Logger LOGGER = LoggerFactory.getLogger(SshService.class);
    private final SshState sshState;
    private final SshKeyLoader sshKeyLoader;
    private SshClient client;
    private boolean clientInitialized = false;
    private final Map<String, ClientSession> activeSessions = new ConcurrentHashMap<>();

    public SshService(SshState sshState,
                      SshKeyLoader sshKeyLoader
    )
    {
        this.sshState = sshState;
        this.sshKeyLoader = sshKeyLoader;
    }

    synchronized public boolean init() {
        if (!clientInitialized) {
            client = SshClient.setUpDefaultClient();
            client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
            client.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, Duration.ofSeconds(30));
            List<Path> availableKeyPaths = sshKeyLoader.getAvailableKeyFilePaths();
            if (availableKeyPaths.isEmpty()) {
                LOGGER.error("No key files available");
            } else {
                Path sshKeyPath = availableKeyPaths.getFirst();
                FileKeyPairProvider keyPairProvider = new FileKeyPairProvider(sshKeyPath);
                if (sshState.getPassPhraseMode()
                        .equals(PassPhraseMode.PROVIDED)) {
                    keyPairProvider.setPasswordFinder(FilePasswordProvider.of(sshState.getPassPhrase()));
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

    private Optional<ClientSession> getOrCreateSession(String host,
                                                       int port
    )
    {
        Optional<ClientSession> optionalSession;
        if (host == null){
            optionalSession = Optional.empty();
        } else {
            optionalSession = Optional.ofNullable(activeSessions.get(host));
        }
        try {
            if (optionalSession.isPresent() && optionalSession.get()
                    .isClosed()) {
                activeSessions.remove(host);
                LOGGER.debug("Removed active session for: {}", host);
            }
            optionalSession = Optional.ofNullable(client.connect(sshState.getSshUsername(), host, port)
                                                          .verify(10, TimeUnit.SECONDS)
                                                          .getSession());
            if (optionalSession.isPresent()) {
                optionalSession.get()
                        .auth()
                        .verify(10, TimeUnit.SECONDS);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception creating session for host: {}", host);
        }
        return optionalSession;
    }

    /**
     * Opens a session to the given host and runs a command, returning its output.
     * Call this per-command for simple use cases.
     */
    public String executeCommand(String host,
                                 int port,
                                 String command
    ) throws Exception
    {
        String response = "";
        Optional<ClientSession> optionalSession = getOrCreateSession(host, port);
        if (clientInitialized && optionalSession.isPresent()) {
            ClientSession session = optionalSession.get();
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
        } else {
            throw new RuntimeException("SSH client not initialized");
        }
        return response;
    }

    /**
     * Opens a session to the given host and runs a command, returning its output.
     * Call this per-command for simple use cases.
     */
    public String executeSudoCommand(String host,
                                     int port,
                                     String command
    ) throws Exception
    {
        String response = "";
        Optional<ClientSession> optionalSession = getOrCreateSession(host, port);
        if (clientInitialized && optionalSession.isPresent()) {
            ClientSession session = optionalSession.get();
            command = "sudo -S -p '' " + command;
            try (ByteArrayOutputStream stdout = new ByteArrayOutputStream(); ByteArrayOutputStream stderr = new ByteArrayOutputStream(); ChannelExec channel = session.createExecChannel(command)) {
                channel.setOut(stdout);
                channel.setErr(stderr);

                // Write password to stdin before opening so it's ready immediately
                byte[] passwordBytes = (sudoKey + "\n").getBytes(StandardCharsets.UTF_8);
                channel.setIn(new ByteArrayInputStream(passwordBytes));

                channel.open()
                        .verify(10, TimeUnit.SECONDS);
                // Wait for the command to finish (or timeout after 30 s)
                channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(30));
                if (stderr.size() > 0) {
                    LOGGER.warn("stderr from [{}]: {}", command, stderr);
                }
                response = stdout.toString(StandardCharsets.UTF_8);
            }
        } else {
            throw new RuntimeException("SSH client not initialized");
        }
        return response;
    }

    public boolean secureCopyFileFromHost(String host,
                                          int port,
                                          String srcAbsolutePath,
                                          String destPath) throws Exception
    {
        boolean fileTransferSuccess = false;
        Optional<ClientSession> optionalSession = getOrCreateSession(host, port);
        if (clientInitialized && optionalSession.isPresent()) {
            ScpClientCreator creator = ScpClientCreator.instance();
            ScpClient scpClient = creator.createScpClient(optionalSession.get());
            scpClient.download(srcAbsolutePath, destPath, ScpClient.Option.Recursive);
            fileTransferSuccess = true;
        }
        return fileTransferSuccess;
    }
}
