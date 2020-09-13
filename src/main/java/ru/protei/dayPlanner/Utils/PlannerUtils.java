package ru.protei.dayPlanner.Utils;

import lombok.SneakyThrows;
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

    private SimpleDateFormat reportDateFormat = new SimpleDateFormat("dd.MM.yy");
    private SimpleDateFormat youtrackDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String taskIdRegex = "[0-9A-Z]+-[0-9]+";
    private String taskEstimatedRegex = "Оценка\\s*(\\d+[чм]\\s*\\d*[м]*)";

    private Pattern taskIdPattern = Pattern.compile(taskIdRegex);
    private Pattern taskEstimatedPattern = Pattern.compile(taskEstimatedRegex);

    private String dateFromPlanRegex = "((\\d\\d[.]){2}\\d\\d)";
    private Pattern dateFromPlanPattern = Pattern.compile(dateFromPlanRegex);

    @SneakyThrows
    public Date getDateFromPlan(String plan) {
        Matcher dateFromPlanMatch = dateFromPlanPattern.matcher(plan);
        dateFromPlanMatch.find();
        return reportDateFormat.parse(dateFromPlanMatch.group(1));
    }

    public String formatDateToYoutrackFormat(Date dateToFormat) {
        return youtrackDateFormat.format(dateToFormat);
    }

    public ArrayList<YoutrackIssue> getTasksFromPlan(String plan) {
        log.info(plan);

        ArrayList<String> tasksId = new ArrayList<>();
        ArrayList<String> tasksEstimation = new ArrayList<>();

        Matcher taskIdMatch = taskIdPattern.matcher(plan);
        Matcher estimationMatch = taskEstimatedPattern.matcher(plan);

        while (taskIdMatch.find()) {
            log.info("Task found : " + taskIdMatch.group());
            tasksId.add(taskIdMatch.group());
        }

        while (estimationMatch.find()) {
            log.info("Estimation found : " + estimationMatch.group());
            tasksEstimation.add(estimationMatch.group().replace("Оценка", "").trim());
        }

        ArrayList<YoutrackIssue> resultTasks = new ArrayList<>();
        for (String taskId : tasksId) {
            YoutrackIssue issue = youtrackRequests.getTaskInfo(taskId);
            issue.setEstimatedFormatted(tasksEstimation.remove(0));
            resultTasks.add(issue);
        }

        log.info("Parsed tasks from plan:");
        for (YoutrackIssue issue : resultTasks) {
            log.info(issue.toString());
        }

        return resultTasks;
    }

    public ArrayList<YoutrackIssue> getAllTaskTrackedOnDay(YoutrackIssue[] youtrackIssues, String author, Date planDate) {
        ArrayList<YoutrackIssue> trackedToday = new ArrayList<>();
        for (YoutrackIssue issue : youtrackIssues) {
            Integer minutesFromTask = youtrackRequests.getMinutesFromTask(issue, author, planDate);
            if (minutesFromTask != 0) {
                log.info("Task " + issue.getIdReadable() + " tracked " + minutesFromTask + "m");
                issue.setTrackedTimeFormatted(formatMinutes(minutesFromTask));
                trackedToday.add(issue);
            }
        }
        return trackedToday;
    }

    public String createReport(ArrayList<YoutrackIssue> trackedTodayTasks, ArrayList<YoutrackIssue> tasksFromPlan, Date planDate) {
        ArrayList<YoutrackIssue> completedTasks = new ArrayList<>();
        ArrayList<YoutrackIssue> notCompletedTasks = new ArrayList<>();
        ArrayList<YoutrackIssue> beyondPlanTasks = new ArrayList<>();
        log.info("Tasks from plan:");
        for (YoutrackIssue task : tasksFromPlan) {
            log.info(task.toString());
        }
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

        log.info("Tracked today:");
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
        report += reportDateFormat.format(planDate) + ":\n\n";
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
