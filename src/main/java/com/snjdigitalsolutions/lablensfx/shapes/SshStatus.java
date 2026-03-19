package com.snjdigitalsolutions.lablensfx.shapes;

import javafx.scene.paint.Color;

public enum SshStatus {

    ONLINE(Color.LIMEGREEN, Color.web("#2d7a2d")),
    OFFLINE(Color.CRIMSON,Color.DARKRED),
    UNKNOWN(Color.GRAY,Color.DARKGRAY);

    private final Color fillColor;
    private final Color circleStroke;

    private SshStatus(Color fill, Color stroke) {
        this.fillColor = fill;
        this.circleStroke = stroke;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Color strokeColor() {
        return circleStroke;
    }
}
