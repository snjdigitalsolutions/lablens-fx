package com.snjdigitalsolutions.lablensfx.shapes;

import javafx.scene.paint.Color;

public enum SshStatus implements IndicatorColorProvider, IndicatorTooltipProvider {

    ONLINE(Color.LIMEGREEN, Color.web("#2d7a2d"),"Online"),
    OFFLINE(Color.CRIMSON,Color.DARKRED, "Offline"),
    UNKNOWN(Color.GRAY,Color.DARKGRAY, "Unknown");

    private final Color fillColor;
    private final Color circleStroke;
    private final String tooltipText;

    private SshStatus(Color fill, Color stroke, String tooltipText) {
        this.fillColor = fill;
        this.circleStroke = stroke;
        this.tooltipText = tooltipText;
    }

    @Override
    public Color fillColor() {
        return fillColor;
    }

    @Override
    public Color strokeColor() {
        return circleStroke;
    }

    @Override
    public String toolTipText(){
        return tooltipText;
    }
}
