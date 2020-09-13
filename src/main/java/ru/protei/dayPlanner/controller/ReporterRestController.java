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
import java.util.Date;

import static ru.protei.dayPlanner.Utils.Utils.getUsername;

@Log
@RestController
public class ReporterRestController {
    @Autowired
    PlannerUtils plannerUtils;
    @Autowired
    YoutrackRequests youtrackRequests;

    @RequestMapping(value = "/Planner/processDayPlan", method = RequestMethod.POST, produces = "application/json; charset=utf-8;")
    public String processDayPlan(@RequestBody String body) {

        ArrayList<YoutrackIssue> plannedTasks = plannerUtils.getTasksFromPlan(body);
        Date planDate = plannerUtils.getDateFromPlan(body);
        YoutrackIssue[] allTasksUpdatedOnPlannedDay = youtrackRequests.getTasksUpdatedOn(planDate);
        // Potential error: when task from plan is updated not on plan day - it will show on plan as unfinished
        ArrayList<YoutrackIssue> allTasksTrackedPlannedDay = plannerUtils.getAllTaskTrackedOnDay(allTasksUpdatedOnPlannedDay, getUsername(), planDate);


        String report = plannerUtils.createReport(allTasksTrackedPlannedDay, plannedTasks, planDate);

        log.info(report);
        return report;
    }
}
