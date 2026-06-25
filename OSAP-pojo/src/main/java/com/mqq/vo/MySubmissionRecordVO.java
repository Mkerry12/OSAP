package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySubmissionRecordVO implements Serializable {

    private Long responseId;
    private Long surveyId;
    private String title;
    private String type;
    private String status;
    private Integer questionCount;
    private String submitAt;
    private CreatorVO creator;
}
