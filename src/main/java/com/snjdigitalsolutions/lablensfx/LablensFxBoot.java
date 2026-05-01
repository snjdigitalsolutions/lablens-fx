package com.snjdigitalsolutions.lablensfx;

import com.snjdigitalsolutions.lablensfx.configuration.LabLensFXConfiguration;
import com.snjdigitalsolutions.springbootutilityfx.application.FxBoot;
import com.snjdigitalsolutions.springbootutilityfx.configuration.ApplicationPreConfiguration;
import com.snjdigitalsolutions.springbootutilityfx.configuration.SpringBootUtilityConfiguration;
import com.snjdigitalsolutions.springbootutilityfx.splash.SplashConfiguration;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Import({LabLensFXConfiguration.class, SpringBootUtilityConfiguration.class})
@EnableScheduling
public class LablensFxBoot implements FxBoot {

    public static void main(String[] args) {
        SplashConfiguration.getInstance().setApplicationName("LabLens");
        ApplicationPreConfiguration.getInstance().setStageWidth(925);
        ApplicationPreConfiguration.getInstance().setStageHeight(800);
        ApplicationPreConfiguration.getInstance().setCssPath("/styles/application.css");
        Application.launch(LabLensFX.class, args);
    }
}
