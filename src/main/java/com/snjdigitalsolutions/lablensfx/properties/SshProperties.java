package com.snjdigitalsolutions.lablensfx.properties;

import com.snjdigitalsolutions.lablensfx.service.PassPhraseMode;
import javafx.beans.property.*;
import org.springframework.stereotype.Component;

@Component
public class SshProperties {

    private final ObjectProperty<PassPhraseMode> passPhraseMode = new SimpleObjectProperty<>(PassPhraseMode.NOT_SET);
    private final StringProperty passPhrase = new SimpleStringProperty();
    private final StringProperty sshUsername = new SimpleStringProperty();

    public PassPhraseMode getPassPhraseMode() {
        return passPhraseMode.get();
    }

    public ObjectProperty<PassPhraseMode> passPhraseModeProperty() {
        return passPhraseMode;
    }

    public String getPassPhrase() {
        return passPhrase.get();
    }

    public StringProperty passPhraseProperty() {
        return passPhrase;
    }

    public String getSshUsername() {
        return sshUsername.get();
    }

    public StringProperty sshUsernameProperty() {
        return sshUsername;
    }
}
