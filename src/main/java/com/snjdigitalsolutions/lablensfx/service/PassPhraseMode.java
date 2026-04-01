package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.shapes.IndicatorColorProvider;
import com.snjdigitalsolutions.lablensfx.shapes.IndicatorTooltipProvider;
import javafx.scene.paint.Color;

public enum PassPhraseMode implements IndicatorColorProvider, IndicatorTooltipProvider {

    PROVIDED(Color.LIMEGREEN, Color.web("#2d7a2d"),"Passphrase Provided"),
    NOT_PROVIDED(Color.CRIMSON,Color.DARKRED, "Passphrase Not Provided"),
    NOT_NEEDED(Color.GRAY,Color.DARKGRAY, "Unknown");

    private final Color fillColor;
    private final Color circleStroke;
    private final String tooltipText;

    PassPhraseMode(Color fillColor, Color circleStroke, String tooltipText) {
        this.fillColor = fillColor;
        this.circleStroke = circleStroke;
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
    public String toolTipText() {
        return tooltipText;
    }
}
