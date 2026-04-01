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

    public SshPassphraseIndicator() {
        passPhraseMode.addListener((obj, oldVal, newVal) -> {
            setColors(newVal);
        });
        tooltip.setText(passPhraseMode.get()
                .name()
                .toLowerCase());
    }

    public void setColors(PassPhraseMode status) {
        super.setColors(status);
        passPhraseMode().setValue(status);
        tooltip.setText(passPhraseMode.get()
                .toolTipText());
    }

    public PassPhraseMode getPassPhraseMode() {
        return passPhraseMode.get();
    }

    public ObjectProperty<PassPhraseMode> passPhraseMode() {
        return passPhraseMode;
    }
}
