package com.snjdigitalsolutions.lablensfx.shapes;

import javafx.scene.control.Tooltip;
import javafx.scene.shape.Circle;


public class StatusIndicator extends Circle {

    protected final Tooltip tooltip = new Tooltip("");

    public StatusIndicator() {
        super(7);
        this.setFill(SshStatus.UNKNOWN.fillColor());
        this.setStroke(SshStatus.UNKNOWN.strokeColor());
        this.setStrokeWidth(1.5);
        Tooltip.install(this, tooltip);
    }

    protected void setColors(IndicatorColorProvider status) {
        this.setFill(status.fillColor());
        this.setStroke(status.strokeColor());
    }

}
