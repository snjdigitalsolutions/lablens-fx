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

    /**
     * Creates a passphrase mode descriptor.
     *
     * @param fillColor    the fill color for the SSH indicator shape
     * @param circleStroke the stroke color for the SSH indicator shape
     * @param tooltipText  the tooltip text describing the mode
     */
    PassPhraseMode(Color fillColor, Color circleStroke, String tooltipText) {
        this.fillColor = fillColor;
        this.circleStroke = circleStroke;
        this.tooltipText = tooltipText;
    }

    /**
     * Returns the indicator fill color.
     *
     * @return the fill color
     */
    @Override
    public Color fillColor() {
        return fillColor;
    }

    /**
     * Returns the indicator stroke color.
     *
     * @return the stroke color
     */
    @Override
    public Color strokeColor() {
        return circleStroke;
    }

    /**
     * Returns the tooltip description for this passphrase mode.
     *
     * @return the tooltip text
     */
    @Override
    public String toolTipText() {
        return tooltipText;
    }
}
