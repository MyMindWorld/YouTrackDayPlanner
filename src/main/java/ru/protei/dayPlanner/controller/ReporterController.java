package ru.protei.dayPlanner.controller;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.dayPlanner.Utils.YoutrackRequests;

import java.util.Optional;

import static ru.protei.dayPlanner.Utils.Utils.getUsername;

@Log
@Controller
public class ReporterController {
    String defaultRequestString = "Состояние: New,Discuss,Active,Review,Test,Paused,Open";

    @Autowired
    YoutrackRequests youtrackRequests;

    @RequestMapping("/Reporter")
    public ModelAndView plannerPage(@RequestParam Optional<String> youtrackIssuesSearchTerm) {
        String requestString = "";
        if (youtrackIssuesSearchTerm.isPresent()) {
            requestString = youtrackIssuesSearchTerm.get();

        } else {
            requestString = defaultRequestString;
        }

        return new ModelAndView("Reporter").addObject("issues", youtrackRequests.getIssuesNames(getUsername(), requestString)).addObject("searchQuery", requestString);
    }
}
