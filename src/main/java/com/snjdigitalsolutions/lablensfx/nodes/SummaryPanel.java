package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.properties.GlobalProperties;
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

    public SummaryPanel(@Value("classpath:/fxml/SummaryPanel.fxml")Resource fxml){
        NodeLoader.load(fxml, this);
    }

    public void performIntialization() {
        headerLabel.setText("Summary Panel");
        countLabel.setText("0");
        countLabel.getStyleClass().add("summary-panel-count-black");
        moreInfoLabel.setText("default configuration");
    }

    public void setHeaderLabelText(String text) {
        this.headerLabel.setText(text);
    }

    public void setCountLabel(Integer count) {
        this.countLabel.setText(count.toString());
    }

    public void setCountLabel(String text) {
        this.countLabel.setText(text);
    }

    public void setCountLabelStyleClass(String clazz) {
        this.countLabel.getStyleClass().clear();
        this.countLabel.getStyleClass().add(clazz);
    }

    public void setMoreInfoLabel(String text) {
        this.moreInfoLabel.setText(text);
    }

}
