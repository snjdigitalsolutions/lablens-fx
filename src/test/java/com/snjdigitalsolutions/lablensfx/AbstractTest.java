package com.snjdigitalsolutions.lablensfx;

import com.snjdigitalsolutions.lablensfx.configuration.LabLensFXConfiguration;
import com.snjdigitalsolutions.lablensfx.service.SshService;
import com.snjdigitalsolutions.lablensfx.utility.EtcOsReleaseParser;
import com.snjdigitalsolutions.lablensfx.utility.KeyDirectoryProvider;
import com.snjdigitalsolutions.lablensfx.utility.SshKeyLoader;
import com.snjdigitalsolutions.springbootutilityfx.configuration.SpringBootUtilityConfiguration;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import({LabLensFXConfiguration.class, SpringBootUtilityConfiguration.class})
@ActiveProfiles("test")
public class AbstractTest {

    @Autowired
    protected KeyDirectoryProvider keyDirectoryProvider;
    @Autowired
    protected SshKeyLoader sshKeyLoader;
    @Autowired
    protected ContentReader contentReader;
    @Autowired
    protected EtcOsReleaseParser etcOsReleaseParser;
    @Autowired
    protected SshService sshService;

    private static boolean toolKitInitialized = false;

    @BeforeAll
    static void initToolkit() {
        if (!toolKitInitialized){
            try {
                Platform.startup(() -> {

                });
                toolKitInitialized = true;
            } catch (Exception e) {
                System.out.println("Platform already started");
            }
        }

    }

}
