package com.snjdigitalsolutions.lablensfx.shapes;

import javafx.scene.control.Tooltip;
import javafx.scene.shape.Circle;


public class StatusIndicator extends Circle {

    protected final Tooltip tooltip = new Tooltip("");

    /**
     * Creates a status indicator circle with default dimensions and unknown-state colors.
     */
    public StatusIndicator() {
        super(7);
        this.setFill(SshStatus.UNKNOWN.fillColor());
        this.setStroke(SshStatus.UNKNOWN.strokeColor());
        this.setStrokeWidth(1.5);
        Tooltip.install(this, tooltip);
    }

    /**
     * Updates the indicator's fill and stroke colors using the given provider.
     *
     * @param status the provider that supplies the fill and stroke colors
     */
    protected void setColors(IndicatorColorProvider status) {
        this.setFill(status.fillColor());
        this.setStroke(status.strokeColor());
    }

}
