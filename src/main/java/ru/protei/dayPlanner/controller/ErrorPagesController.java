package ru.protei.dayPlanner.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ErrorPagesController {
    Logger logger = LoggerFactory.getLogger(ErrorPagesController.class);

    @RequestMapping("/ErrorCodes/403")
    public String Forbidden() {
        return "ErrorCodes/403";
    }

    @RequestMapping("/ErrorCodes/500")
    @ExceptionHandler
    public String InternalServerError() {

        return "ErrorCodes/500";
    }
}
