package com.mqq.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SurveyDTO implements Serializable {

    private String title;

    private String description;

    private String type;//PUBLIC(公开) / ASSIGNED(指定用户)，默认 PUBLIC

    private List<String> targetPhones;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer isAnonymous;

    private String theme;

    private Integer allowMultiSubmit;

}
