package com.snjdigitalsolutions.lablensfx.shapes;

import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.shape.Circle;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class StatusIndicator extends Circle {

    private final ObjectProperty<SshStatus> hostSshStatus = new SimpleObjectProperty<>(SshStatus.UNKNOWN);

    public StatusIndicator() {
        super(7);
        this.setFill(SshStatus.UNKNOWN.getFillColor());
        this.setStroke(SshStatus.UNKNOWN.strokeColor());
        this.setStrokeWidth(1.5);
        hostSshStatus.addListener((obj, oldVal, newVal) -> {
            setColors(newVal);
        });
    }

    private void setColors(SshStatus status) {
        hostSshStatusProperty().setValue(status);
        this.setFill(status.getFillColor());
        this.setStroke(status.strokeColor());
    }

    public SshStatus getHostSshStatus() {
        return hostSshStatus.get();
    }

    public ObjectProperty<SshStatus> hostSshStatusProperty() {
        return hostSshStatus;
    }
}
