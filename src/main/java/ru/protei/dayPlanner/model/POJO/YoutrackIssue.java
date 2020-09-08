package ru.protei.dayPlanner.model.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutrackIssue {
    private String idReadable;
    private String summary;
    private String trackedTimeFormatted;
    private String minutes;
    private String estimatedFormatted;

}
