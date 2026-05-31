package com.snjdigitalsolutions.lablensfx.utility;

import com.snjdigitalsolutions.lablensfx.setting.Interval;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;

public class ComboBoxResizingUtility {

    private static final double ARROW_BUTTON_WIDTH = 60.0;

    public static void resizeToContent(ComboBox<Interval> comboBox) {
        Text textNode = new Text();
        textNode.setFont(comboBox.getEditor()
                                 .getFont());
        double maxWidth = 0;
        for (Interval interval : comboBox.getItems()) {
            String label = comboBox.getConverter() != null
                    ? comboBox.getConverter().toString(interval)
                    : interval.displayValue();
            textNode.setText(label);
            maxWidth = Math.max(maxWidth, textNode.getBoundsInLocal().getWidth());
        }
        comboBox.setPrefWidth(maxWidth + ARROW_BUTTON_WIDTH);
    }

}
