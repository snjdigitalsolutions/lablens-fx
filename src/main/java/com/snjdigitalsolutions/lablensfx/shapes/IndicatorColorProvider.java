package com.snjdigitalsolutions.lablensfx.shapes;

import javafx.scene.paint.Color;

public interface IndicatorColorProvider {

    /**
     * Returns the fill color for the indicator shape.
     *
     * @return the fill color
     */
    Color fillColor();

    /**
     * Returns the stroke color for the indicator shape.
     *
     * @return the stroke color
     */
    Color strokeColor();

}
