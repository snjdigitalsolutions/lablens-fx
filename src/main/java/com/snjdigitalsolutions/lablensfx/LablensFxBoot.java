package com.snjdigitalsolutions.lablensfx;

import com.snjdigitalsolutions.lablensfx.configuration.LabLensFXConfiguration;
import com.snjdigitalsolutions.springbootutilityfx.configuration.SpringBootUtilityConfiguration;
import com.snjdigitalsolutions.springbootutilityfx.splash.SplashConfiguration;
import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.awt.*;

@SpringBootApplication
@Import({LabLensFXConfiguration.class, SpringBootUtilityConfiguration.class})
public class LablensFxBoot {

    public static void main(String[] args) {
        SplashConfiguration splashConfiguration = SplashConfiguration.getInstance();
        splashConfiguration.setApplicationName("LabLens");
        Toolkit.getDefaultToolkit();
        Application.launch(LabLensFX.class, args);
    }

}
