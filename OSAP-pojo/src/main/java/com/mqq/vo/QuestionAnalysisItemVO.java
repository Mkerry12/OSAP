package com.mqq.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionAnalysisItemVO implements Serializable {
    private Long questionId;
    private String questionTitle;
    private String questionType;
    private Integer totalAnswers;
    private Integer skipCount;
    private QuestionStatisticsVO statistics;
}
