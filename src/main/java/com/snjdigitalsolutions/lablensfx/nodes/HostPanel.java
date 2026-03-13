package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class HostPanel extends GridPane implements SpringInitializableNode {

    @FXML
    private Label hostNameLabel;
    @FXML
    private Label ipAddressLabel;
    @FXML
    private Label osLabel;
    @FXML
    private Label descriptionLabel;

    public HostPanel(@Value("classpath:/fxml/HostPanel.fxml") Resource fxml){
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {

    }

    public Label getHostNameLabel() {
        return hostNameLabel;
    }

    public Label getIpAddressLabel() {
        return ipAddressLabel;
    }

    public Label getOsLabel() {
        return osLabel;
    }

    public Label getDescriptionLabel() {
        return descriptionLabel;
    }
}
