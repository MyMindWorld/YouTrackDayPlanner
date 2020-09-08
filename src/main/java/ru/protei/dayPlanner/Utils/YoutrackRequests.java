package ru.protei.dayPlanner.Utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;
import ru.protei.dayPlanner.model.POJO.YoutrackIssue;
import ru.protei.dayPlanner.model.POJO.YoutrackWorkItem;

import java.util.Date;

@Log
@Component
public class YoutrackRequests {

    String youtrackSupportToken = "perm:Z3JvbW92X3A=.NjEtNTU=.cJ1gwI3AomMNvGWlrPVbFqywUSpMzc";
    String author = "gromov_p";

    private RequestSpecification getYoutrackSpec() {
        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://youtrack.protei.ru/api/issues?query=")
                .addHeader("Accept", ContentType.JSON.toString())
                .addHeader("Content-Type", ContentType.JSON.toString())
                .addHeader("Authorization", "Bearer " + youtrackSupportToken)
                .log(LogDetail.ALL)
                .build();
        return requestSpecification;
    }

    public YoutrackIssue[] getTasksUpdatedToday() {
        return RestAssured.given()
                .spec(getYoutrackSpec())
                .baseUri("")
                .when()
                .get("https://youtrack.protei.ru/api/issues?query=обновлена:Вчера проект: CRM-test,EQA,VP &fields=idReadable,summary")
                .then()
                .extract()
                .as(YoutrackIssue[].class);
    }

    public Integer getMinutesFromTask(YoutrackIssue issue, String author) {
        YoutrackWorkItem[] workItemsFromTask = RestAssured.given()
                .spec(getYoutrackSpec())
                .baseUri("https://youtrack.protei.ru/rest/issue/" + issue.getIdReadable() + "/timetracking/workitem/")
                .when()
                .get()
                .then()
                .extract()
                .as(YoutrackWorkItem[].class);

        Integer trackedMinutes = 0;
        for (YoutrackWorkItem workItem : workItemsFromTask) {
            log.info(workItem.toString());
            if (workItem.getAuthor().getLogin().equals(author) & DateUtils.isSameDay(workItem.getCreated(), new Date())) {
                trackedMinutes += workItem.getDuration();
            }
        }
        return trackedMinutes;
    }

    @SneakyThrows
    public YoutrackIssue[] getIssuesNames(String userToSearch, String requestString) {
        String bearerToken = "perm:c3VwcG9ydA==.NjEtMzk=.3uX15I2FE77MOXB55mXj7mFgQOfSyt"; // Support token. Todo to props
        String requestUrl = String.format("https://youtrack.protei.ru/api/issues?fields=idReadable,summary&query=(Исполнитель: %s или Рецензент: %s) и " + requestString, userToSearch, userToSearch);
        RestAssured.defaultParser = Parser.JSON;
        String issuesStr = RestAssured.given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .when()
                .urlEncodingEnabled(true)
                .get(requestUrl)
                .then()
                .extract()
                .asString();
        // todo log
        ObjectMapper jsonMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        YoutrackIssue[] issues = jsonMapper.readValue(issuesStr, YoutrackIssue[].class);
        return issues;
    }
}
