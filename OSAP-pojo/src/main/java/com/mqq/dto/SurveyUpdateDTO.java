package com.mqq.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SurveyUpdateDTO implements Serializable {

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String theme;
}
