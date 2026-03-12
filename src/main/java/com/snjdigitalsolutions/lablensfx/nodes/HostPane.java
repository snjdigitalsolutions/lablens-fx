package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.springbootutilityfx.node.utility.NodeLoader;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class HostPane extends AnchorPane {

    public HostPane(@Value("classpath:/fxml/HostPane.fxml") Resource fxml) {
        NodeLoader.load(fxml, this);
    }

}
