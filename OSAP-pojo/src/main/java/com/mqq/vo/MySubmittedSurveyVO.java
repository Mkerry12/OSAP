package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySubmittedSurveyVO implements Serializable {

    private Long id;
    private String title;
    private String description;
    private String type;
    private String status;
    private Integer questionCount;
    private Integer allowMultiSubmit;
    private String endTime;
    private String submittedAt;
    private CreatorVO creator;
}
