package com.snjdigitalsolutions.lablensfx.state;

import com.snjdigitalsolutions.lablensfx.service.PassPhraseMode;
import javafx.beans.property.*;
import org.springframework.stereotype.Component;

@Component
public class SshState {

    private final ObjectProperty<PassPhraseMode> passPhraseMode = new SimpleObjectProperty<>();
    private final StringProperty passPhrase = new SimpleStringProperty();
    private final StringProperty sshUsername = new SimpleStringProperty();

    /**
     * Returns the current passphrase mode (e.g. configured, unconfigured).
     *
     * @return the active {@link PassPhraseMode}
     */
    public PassPhraseMode getPassPhraseMode() {
        return passPhraseMode.get();
    }

    /**
     * Returns the observable property for the current passphrase mode.
     *
     * @return the passphrase-mode property
     */
    public ObjectProperty<PassPhraseMode> passPhraseModeProperty() {
        return passPhraseMode;
    }

    /**
     * Returns the SSH key passphrase entered by the user.
     *
     * @return the passphrase string
     */
    public String getPassPhrase() {
        return passPhrase.get();
    }

    /**
     * Returns the observable string property for the SSH passphrase.
     *
     * @return the passphrase property
     */
    public StringProperty passPhraseProperty() {
        return passPhrase;
    }

    /**
     * Returns the SSH username used for remote connections.
     *
     * @return the SSH username string
     */
    public String getSshUsername() {
        return sshUsername.get();
    }

    /**
     * Returns the observable string property for the SSH username.
     *
     * @return the SSH username property
     */
    public StringProperty sshUsernameProperty() {
        return sshUsername;
    }
}
