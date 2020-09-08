package ru.protei.dayPlanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;



@SpringBootApplication
public class DayPlanner extends SpringBootServletInitializer {
    Logger logger = LoggerFactory.getLogger(DayPlanner.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DayPlanner.class);
    }

    public static void main(String[] args) throws Exception {
        String configLocation = System.getProperty("spring.config.location"); //Get the default config directory
        if (configLocation == null){
            configLocation = "classpath:/";
        }
        ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(DayPlanner.class)
                .properties("spring.config.name:script-server,application", // First entry here is prioritized by Spring
                        "spring.config.location:" + "classpath:/," + configLocation) // Same as above
                .build()
                .run(args);
    }

}
