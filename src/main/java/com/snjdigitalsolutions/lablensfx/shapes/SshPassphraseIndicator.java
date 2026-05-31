package com.snjdigitalsolutions.lablensfx.shapes;

import com.snjdigitalsolutions.lablensfx.service.PassPhraseMode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SshPassphraseIndicator extends StatusIndicator {

    private final ObjectProperty<PassPhraseMode> passPhraseMode = new SimpleObjectProperty<>(PassPhraseMode.NOT_PROVIDED);

    /**
     * Creates a new SSH passphrase indicator with the default {@code NOT_PROVIDED} state.
     */
    public SshPassphraseIndicator() {
        passPhraseMode.addListener((obj, oldVal, newVal) -> {
            setColors(newVal);
        });
        tooltip.setText(passPhraseMode.get()
                .name()
                .toLowerCase());
    }

    /**
     * Applies the fill and stroke colors defined by the given passphrase mode.
     *
     * @param status the passphrase mode whose colors should be applied
     */
    public void setColors(PassPhraseMode status) {
        super.setColors(status);
        passPhraseMode().setValue(status);
        tooltip.setText(passPhraseMode.get()
                .toolTipText());
    }

    /**
     * Returns the current passphrase mode value.
     *
     * @return the active {@link PassPhraseMode}
     */
    public PassPhraseMode getPassPhraseMode() {
        return passPhraseMode.get();
    }

    /**
     * Returns the observable property for the current passphrase mode.
     *
     * @return the passphrase mode property
     */
    public ObjectProperty<PassPhraseMode> passPhraseMode() {
        return passPhraseMode;
    }
}
