package ru.protei.dayPlanner.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Component
public class Utils {
    Logger logger = LoggerFactory.getLogger(Utils.class);

    @Value("${TomcatPath:NotSet}")
    public String tomcatPath;
    String webappFolder = "/src/main/webapp";

    @PostConstruct
    public void UtilsCreation() {
        if (tomcatPath.equals("NotSet")) {
            logger.info("Tomcat path not found, using system property user.dir");
            tomcatPath = System.getProperty("user.dir") + webappFolder;
        } else {
            logger.info("Tomcat path found!");
            logger.info("Now it set to : '" + tomcatPath + "'");
        }

        assert !tomcatPath.equals("NotSet");
    }

    public String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    public static String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }
}
