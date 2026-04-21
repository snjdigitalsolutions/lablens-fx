package com.snjdigitalsolutions.lablensfx.nodes;

import com.snjdigitalsolutions.lablensfx.state.ApplicationState;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class LoadingOverlay extends VBox  implements SpringInitializableNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadingOverlay.class);
    private final Resource imageResource;
    private final ApplicationState applicationState;

    public LoadingOverlay(@Value("classpath:/images/spinner-96-tsp.gif") Resource imageResource,
                          ApplicationState applicationState
    ){
        this.imageResource = imageResource;
        this.applicationState = applicationState;
    }

    @Override
    public void performIntialization() {
        this.setAlignment(Pos.CENTER);
        try {
            ImageView imageView = new ImageView(imageResource.getURL().toString());
            Label loadingLabel = new Label("Loading...");
            loadingLabel.getStyleClass().add("loading-text");
            this.setVisible(false);
            this.getChildren().addAll(imageView, loadingLabel);
            this.visibleProperty().bind(applicationState.loadingDataProperty());
        } catch (Exception e) {
            LOGGER.error("Unable to load overlay image");
        }
    }
}
