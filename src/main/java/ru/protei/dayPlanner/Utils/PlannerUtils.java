package ru.protei.dayPlanner.Utils;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.protei.dayPlanner.model.POJO.YoutrackIssue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
@Component
public class PlannerUtils {
    @Autowired
    YoutrackRequests youtrackRequests;

    final String DATE_FORMAT_NOW = "dd.MM.yy";
    SimpleDateFormat reportDateFormat = new SimpleDateFormat(DATE_FORMAT_NOW);

    String taskIdRegex = "[0-9A-Z]+-[0-9]+";
    String taskEstimatedRegex = "([0-9]+.*)]";

    Pattern taskIdPattern = Pattern.compile(taskIdRegex);
    Pattern taskEstimatedPattern = Pattern.compile(taskEstimatedRegex);

    public ArrayList<YoutrackIssue> getTasksFromPlan(String plan) {
        ArrayList<String> tasksId = new ArrayList<>();
        ArrayList<String> tasksEstimation = new ArrayList<>();

        Matcher taskIdMatch = taskIdPattern.matcher(plan);
        Matcher estimationMatch = taskEstimatedPattern.matcher(plan);

        while (taskIdMatch.find()) {
            log.info(taskIdMatch.group());
            tasksId.add(taskIdMatch.group());
        }

        while (estimationMatch.find()) {
            log.info(estimationMatch.group());
            tasksEstimation.add(estimationMatch.group());
        }

        ArrayList<YoutrackIssue> resultTasks = new ArrayList<>();
        for (String taskId : tasksId) {
            YoutrackIssue issue = YoutrackIssue.builder()
                    .idReadable(taskId)
                    .estimatedFormatted(tasksEstimation.remove(0))
                    .build();
            resultTasks.add(issue);
        }

        for (YoutrackIssue issue : resultTasks) {
            log.info(issue.toString());

        }

        return resultTasks;
    }

    public ArrayList<YoutrackIssue> getAllTaskTrackedToday(YoutrackIssue[] youtrackIssues, String author) {
        ArrayList<YoutrackIssue> trackedToday = new ArrayList<>();
        for (YoutrackIssue issue : youtrackIssues) {
            Integer minutesFromTask = youtrackRequests.getMinutesFromTask(issue, author);
            if (minutesFromTask != 0) {
                log.info("Task " + issue.getIdReadable() + " tracked " + minutesFromTask + "m");
                issue.setTrackedTimeFormatted(formatMinutes(minutesFromTask));
                trackedToday.add(issue);
            }
        }
        return trackedToday;
    }

    public String createReport(ArrayList<YoutrackIssue> trackedTodayTasks, ArrayList<YoutrackIssue> tasksFromPlan) {
        ArrayList<YoutrackIssue> completedTasks = new ArrayList<>();
        ArrayList<YoutrackIssue> notCompletedTasks = new ArrayList<>();
        ArrayList<YoutrackIssue> beyondPlanTasks = new ArrayList<>();
        for (YoutrackIssue trackedTodayTask : trackedTodayTasks) {
            Optional<YoutrackIssue> completedTaskFromPlan = tasksFromPlan.stream().filter(task -> task.getIdReadable().equals(trackedTodayTask.getIdReadable())).findFirst();
            if (completedTaskFromPlan.isPresent()) {
                completedTasks.add(trackedTodayTask);
                tasksFromPlan.remove(completedTaskFromPlan.get());
            } else {
                beyondPlanTasks.add(trackedTodayTask);
            }
        }
        notCompletedTasks.addAll(tasksFromPlan);

        log.info("Completed:");
        for (YoutrackIssue task : trackedTodayTasks) {
            log.info(task.toString());
        }

        log.info("Not Completed:");
        for (YoutrackIssue task : notCompletedTasks) {
            log.info(task.toString());
        }

        log.info("Beyond plan:");
        for (YoutrackIssue task : beyondPlanTasks) {
            log.info(task.toString());
        }

        String report = "Отчёт за ";
        report += reportDateFormat.format(new Date()) + ":\n\n";
        report += printFormattedTaskList(completedTasks, true, "[Выполнено]:");
        report += printFormattedTaskList(notCompletedTasks, false, "[Не выполнено]:");
        report += printFormattedTaskList(beyondPlanTasks, true, "[Сверх плана]:");

        return report;
    }

    public String printFormattedTaskList(ArrayList<YoutrackIssue> taskList, Boolean isCompleted, String prefix) {
        if (taskList.size() == 0) {
            return "";
        }

        String result = prefix + "\n";

        if (isCompleted) {
            for (YoutrackIssue task : taskList) {
                result += " * [Затрачено: " + task.getTrackedTimeFormatted() + "] " + task.getIdReadable() + " " + task.getSummary() + "\n";
            }
        } else {
            for (YoutrackIssue task : taskList) {
                result += " * [Оценка: " + task.getEstimatedFormatted() + "] " + task.getIdReadable() + " " + task.getSummary() + "\n";
            }
        }
        return result;
    }

    public String formatMinutes(Integer minutes) {
        Integer hours = minutes / 60;
        Integer leftMinutes = minutes % 60;
        String resultFormat = "";
        if (hours > 0) {
            resultFormat += hours + "ч";
        }
        if (hours > 0 & leftMinutes > 0) {
            resultFormat += " ";
        }
        if (leftMinutes > 0) {
            resultFormat += leftMinutes + "м";
        }
        return resultFormat;

    }

}
