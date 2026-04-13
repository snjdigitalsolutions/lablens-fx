package com.snjdigitalsolutions.lablensfx;

import com.snjdigitalsolutions.lablensfx.configuration.LabLensFXConfiguration;
import com.snjdigitalsolutions.lablensfx.state.SshState;
import com.snjdigitalsolutions.lablensfx.service.PassPhraseMode;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import com.snjdigitalsolutions.lablensfx.service.command.CheckElevatedPrivilegesRequiredCommand;
import com.snjdigitalsolutions.lablensfx.state.ElevatedPrivilegedPathState;
import com.snjdigitalsolutions.lablensfx.service.command.ListFileCommand;
import com.snjdigitalsolutions.lablensfx.service.command.commandparser.ListFileParser;
import com.snjdigitalsolutions.lablensfx.utility.EtcOsReleaseParser;
import com.snjdigitalsolutions.lablensfx.utility.KeyDirectoryProvider;
import com.snjdigitalsolutions.lablensfx.utility.SshKeyLoader;
import com.snjdigitalsolutions.springbootutilityfx.configuration.SpringBootUtilityConfiguration;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import({LabLensFXConfiguration.class, SpringBootUtilityConfiguration.class})
@ActiveProfiles("test")
public class AbstractTest {

    private static final AtomicBoolean platformStarted = new AtomicBoolean(false);

    @Value("${application.ssh.username}")
    protected String username;
    @Value("${application.ssh.passphrase}")
    protected String passPhrase;
    @Value("${application.ssh.testhost}")
    protected String testhost;
    @Value("${application.ssh.testip}")
    protected String ipAddress;

    @Autowired
    protected KeyDirectoryProvider keyDirectoryProvider;
    @Autowired
    protected SshKeyLoader sshKeyLoader;
    @Autowired
    protected EtcOsReleaseParser etcOsReleaseParser;
    @Autowired
    protected SshService sshService;
    @Autowired
    protected SshState sshState;
    @Autowired
    protected CheckElevatedPrivilegesRequiredCommand checkElevatedPrivilegesRequiredCommand;
    @Autowired
    protected ListFileCommand listFileCommand;
    @Autowired
    protected ListFileParser listFileParser;
    @Autowired
    protected ElevatedPrivilegedPathState elevatedPrivilegedPathState;

    /**
     * Specifically for testing
     */
    @Autowired
    protected ContentReader contentReader;

    @BeforeAll
    static void initToolkit() {
        if (platformStarted.compareAndSet(false, true)) {
            Platform.startup(() -> {});
        }
    }

    public void setSshProperties(){
        sshState.sshUsernameProperty()
                .setValue(username);
        sshState.passPhraseProperty()
                .setValue(passPhrase);
        sshState.passPhraseModeProperty()
                .setValue(PassPhraseMode.PROVIDED);
    }

}
