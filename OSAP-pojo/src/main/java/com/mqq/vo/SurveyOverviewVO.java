package com.mqq.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SurveyOverviewVO implements Serializable {
    private Long surveyId;
    private String title;
    private Integer totalResponses;
    private Double completionRate;
    private Integer averageDuration;
    private Integer questionCount;
    private List<DailyResponseVO> dailyResponses;
}
