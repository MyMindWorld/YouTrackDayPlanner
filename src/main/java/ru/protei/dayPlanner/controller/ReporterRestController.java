package ru.protei.dayPlanner.controller;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.dayPlanner.Utils.PlannerUtils;
import ru.protei.dayPlanner.Utils.YoutrackRequests;
import ru.protei.dayPlanner.model.POJO.YoutrackIssue;

import java.util.ArrayList;

import static ru.protei.dayPlanner.Utils.Utils.getUsername;

@Log
@RestController
public class ReporterRestController {
    @Autowired
    PlannerUtils plannerUtils;
    @Autowired
    YoutrackRequests youtrackRequests;

    @RequestMapping(value = "/Planner/processDayPlan", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public String processDayPlan(@RequestBody String body) {
        YoutrackIssue[] allTasksUpdatedToday = youtrackRequests.getTasksUpdatedToday();
        ArrayList<YoutrackIssue> allTasksTrackedToday = plannerUtils.getAllTaskTrackedToday(allTasksUpdatedToday, getUsername());
        ArrayList<YoutrackIssue> plannedTasks = plannerUtils.getTasksFromPlan(body);

        String report = plannerUtils.createReport(allTasksTrackedToday, plannedTasks);

        log.info(report);
        return report;
    }
}
