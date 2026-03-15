package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class DashboardPane extends AnchorPane implements SpringInitializableNode {


    public DashboardPane(@Value("classpath:/fxml/DashboardPane.fxml") Resource fxml){
        NodeLoader.load(fxml, this);
    }

    @Override
    public void performIntialization() {

    }
}
