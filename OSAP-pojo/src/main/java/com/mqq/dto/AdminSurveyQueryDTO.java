package com.mqq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminSurveyQueryDTO implements Serializable {

    private Integer page;

    private Integer size;

    private String status;
}
