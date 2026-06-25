package com.mqq.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmittedRecordRow {
    private Long submissionId;
    private LocalDateTime submitAt;
    private Long surveyId;
    private String title;
    private String type;
    private String status;
    private Integer questionCount;
    private Long creatorId;
}
