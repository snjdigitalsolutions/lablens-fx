package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class HostPanelLarge extends GridPane {

    @FXML
    @Getter
    private Label hostNameLabel;
    @FXML
    @Getter
    private Label ipAddressLabel;

    public HostPanelLarge(@Value("classpath:/fxml/HostPanelLarge.fxml") Resource fxml) {
        NodeLoader.load(fxml, this);
    }


}
