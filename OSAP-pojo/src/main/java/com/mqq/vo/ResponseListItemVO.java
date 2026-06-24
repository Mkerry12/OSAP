package com.mqq.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseListItemVO implements Serializable {
    private Long id;
    private String respondent;
    private String submittedAt;
    private Integer duration;
}
