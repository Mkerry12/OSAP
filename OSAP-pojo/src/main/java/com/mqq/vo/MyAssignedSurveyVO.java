package com.mqq.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyAssignedSurveyVO implements Serializable {

    private Long id;
    private String title;
    private String description;
    private String type;
    private Integer questionCount;
    private String endTime;
    private CreatorVO creator;
    private String createdAt;
}
