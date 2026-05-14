package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SummaryPanel extends HBox {

    @FXML
    private Label headerLabel;
    @FXML
    private Label countLabel;
    @FXML
    private Label moreInfoLabel;

    /**
     * Creates a summary panel attached to the given FXML resource.
     *
     * @param fxml the FXML resource for the summary panel layout
     */
    public SummaryPanel(@Value("classpath:/fxml/SummaryPanel.fxml")Resource fxml){
        NodeLoader.load(fxml, this);
    }

    /**
     * Initializes the panel layout with default label values and styling.
     */
    public void performIntialization() {
        headerLabel.setText("Summary Panel");
        countLabel.setText("0");
        countLabel.getStyleClass().add("summary-panel-count-black");
        moreInfoLabel.setText("default configuration");
    }

    /**
     * Sets the panel's header label text.
     *
     * @param text the header string to display
     */
    public void setHeaderLabelText(String text) {
        this.headerLabel.setText(text);
    }

    /**
     * Sets the count label to the given integer value.
     *
     * @param count the number to display
     */
    public void setCountLabel(Integer count) {
        this.countLabel.setText(count.toString());
    }

    /**
     * Sets the count label to the given string value.
     *
     * @param text the string to display in the count label
     */
    public void setCountLabel(String text) {
        this.countLabel.setText(text);
    }

    /**
     * Replaces the count label's CSS style class.
     *
     * @param clazz the style class name to apply
     */
    public void setCountLabelStyleClass(String clazz) {
        this.countLabel.getStyleClass().clear();
        this.countLabel.getStyleClass().add(clazz);
    }

    /**
     * Sets the supplemental information label text.
     *
     * @param text the detail string to display beneath the count
     */
    public void setMoreInfoLabel(String text) {
        this.moreInfoLabel.setText(text);
    }

}
