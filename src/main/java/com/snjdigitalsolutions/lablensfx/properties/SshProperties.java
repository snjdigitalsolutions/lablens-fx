package com.snjdigitalsolutions.lablensfx.properties;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.springframework.stereotype.Component;

@Component
public class SshProperties {

    private final BooleanProperty passPhraseSet = new SimpleBooleanProperty(false);
    private final BooleanProperty passPhraseNotNeeded = new SimpleBooleanProperty(false);
    private final StringProperty passPhrase = new SimpleStringProperty();
    private final StringProperty sshUsername = new SimpleStringProperty();

    public String getPassPhrase() {
        return passPhrase.get();
    }

    public StringProperty passPhraseProperty() {
        return passPhrase;
    }

    public boolean isPassPhraseSet() {
        return passPhraseSet.get();
    }

    public BooleanProperty passPhraseSetProperty() {
        return passPhraseSet;
    }

    public String getSshUsername() {
        return sshUsername.get();
    }

    public StringProperty sshUsernameProperty() {
        return sshUsername;
    }

    public boolean isPassPhraseNotNeeded() {
        return passPhraseNotNeeded.get();
    }

    public BooleanProperty passPhraseNotNeededProperty() {
        return passPhraseNotNeeded;
    }
}
