package com.snjdigitalsolutions.lablensfx.shapes;

import javafx.scene.paint.Color;

public enum SshStatus implements IndicatorColorProvider, IndicatorTooltipProvider {

    ONLINE(Color.LIMEGREEN, Color.web("#2d7a2d"),"Online"),
    OFFLINE(Color.CRIMSON,Color.DARKRED, "Offline"),
    UNKNOWN(Color.GRAY,Color.DARKGRAY, "Unknown");

    private final Color fillColor;
    private final Color circleStroke;
    private final String tooltipText;

    /**
     * Creates an SSH status descriptor.
     *
     * @param fill        the fill color for the status indicator shape
     * @param stroke      the stroke color for the status indicator shape
     * @param tooltipText the tooltip text describing this status
     */
    private SshStatus(Color fill, Color stroke, String tooltipText) {
        this.fillColor = fill;
        this.circleStroke = stroke;
        this.tooltipText = tooltipText;
    }

    /**
     * Returns the fill color for this status.
     *
     * @return the fill color
     */
    @Override
    public Color fillColor() {
        return fillColor;
    }

    /**
     * Returns the stroke color for this status.
     *
     * @return the stroke color
     */
    @Override
    public Color strokeColor() {
        return circleStroke;
    }

    /**
     * Returns the tooltip description for this SSH status.
     *
     * @return the tooltip text
     */
    @Override
    public String toolTipText(){
        return tooltipText;
    }
}
