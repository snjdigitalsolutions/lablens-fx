package com.snjdigitalsolutions.lablensfx.shapes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SshStatusIndicator extends StatusIndicator {

    private final ObjectProperty<SshStatus> hostSshStatus = new SimpleObjectProperty<>(SshStatus.UNKNOWN);

    /**
     * Creates a new SSH status indicator circle with default {@code UNKNOWN} colors.
     */
    public SshStatusIndicator() {
        hostSshStatus.addListener((obj, oldVal, newVal) -> {
            setColors(newVal);
        });
        tooltip.setText(hostSshStatus.get().name().toLowerCase());
    }

    /**
     * Applies the fill and stroke colors defined by the given SSH status.
     *
     * @param status the SSH status whose colors should be applied
     */
    public void setColors(SshStatus status){
        super.setColors(status);
        hostSshStatusProperty().setValue(status);
        tooltip.setText(hostSshStatus.get().toolTipText());
    }

    /**
     * Returns the current SSH status value.
     *
     * @return the active {@link SshStatus}
     */
    public SshStatus getHostSshStatus() {
        return hostSshStatus.get();
    }

    /**
     * Returns the observable property for the current SSH status.
     *
     * @return the SSH status property
     */
    public ObjectProperty<SshStatus> hostSshStatusProperty() {
        return hostSshStatus;
    }
}
