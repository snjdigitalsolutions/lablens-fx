package com.snjdigitalsolutions.lablensfx.application;

import com.snjdigitalsolutions.lablensfx.nodes.HostPane;
import com.snjdigitalsolutions.springbootutilityfx.event.StageReadyEvent;
import com.snjdigitalsolutions.springbootutilityfx.node.SpringInitializableNode;
import com.snjdigitalsolutions.springbootutilityfx.node.utility.MinimizeUtility;
import com.snjdigitalsolutions.springbootutilityfx.splash.Splash;
import com.snjdigitalsolutions.springbootutilityfx.splash.SplashController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageReadyListener implements ApplicationListener<StageReadyEvent>, SpringInitializableNode {

    private final Resource fxml;
    private final ApplicationContext applicationContext;
    private final MinimizeUtility minimizeUtility;
    private final StageReadyController stageReadyController;

    public StageReadyListener(@Value("classpath:/fxml/RootPane.fxml") Resource fxml, ApplicationContext applicationContext, MinimizeUtility minimizeUtility, HostPane hostPane, StageReadyController stageReadyController){
        this.fxml = fxml;
        this.applicationContext = applicationContext;
        this.minimizeUtility = minimizeUtility;
        this.stageReadyController = stageReadyController;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            Stage applicationStage = event.getStage();
            applicationStage.setWidth(1000);
            applicationStage.setHeight(800);
            SplashController.setStage(applicationStage);
            FXMLLoader fxmlLoader = new FXMLLoader(fxml.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent applicationRoot = fxmlLoader.load();
            Scene applicationScene = new Scene(applicationRoot);
            applicationStage.setScene(applicationScene);
            minimizeUtility.addMinimizeToScene(applicationScene, applicationStage);
            stageReadyController.addHostPane();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void performIntialization() {

    }
}
