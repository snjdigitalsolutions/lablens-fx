package com.snjdigitalsolutions.lablensfx.shapes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SshStatusIndicator extends StatusIndicator {

    private final ObjectProperty<SshStatus> hostSshStatus = new SimpleObjectProperty<>(SshStatus.UNKNOWN);

    public SshStatusIndicator() {
        hostSshStatus.addListener((obj, oldVal, newVal) -> {
            setColors(newVal);
        });
        tooltip.setText(hostSshStatus.get().name().toLowerCase());
    }

    public void setColors(SshStatus status){
        super.setColors(status);
        hostSshStatusProperty().setValue(status);
        tooltip.setText(hostSshStatus.get().toolTipText());
    }

    public SshStatus getHostSshStatus() {
        return hostSshStatus.get();
    }

    public ObjectProperty<SshStatus> hostSshStatusProperty() {
        return hostSshStatus;
    }
}
