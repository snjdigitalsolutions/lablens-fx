package com.snjdigitalsolutions.lablensfx;

import com.snjdigitalsolutions.springbootutilityfx.event.StageReadyEvent;
import com.snjdigitalsolutions.springbootutilityfx.splash.SplashConfiguration;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class LabLensFX extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() throws Exception {
        ApplicationContextInitializer<GenericApplicationContext> initializer =
                applicationContext -> {
                    applicationContext.registerBean(Application.class, () -> LabLensFX.this);
                    applicationContext.registerBean(Parameters.class, this::getParameters);
                    applicationContext.registerBean(HostServices.class, this::getHostServices);
                };

        springContext = new SpringApplicationBuilder()
                .sources(LablensFxBoot.class)
                .initializers(initializer)
                .listeners(SplashConfiguration.getInstance().springEventListener(this))
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        springContext.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
        Platform.exit();
    }

}
