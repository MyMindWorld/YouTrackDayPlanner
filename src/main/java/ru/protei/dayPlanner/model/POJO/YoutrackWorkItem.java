package ru.protei.dayPlanner.model.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutrackWorkItem {
   private Integer duration;
   private String description;
   private Author author;
   private Date created;

    @Data
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        private String login;
    }
}
