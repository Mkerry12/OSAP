package com.mqq.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AnswerDetailVO implements Serializable {

    private Long questionId;
    private String questionTitle;
    private String questionType;
    private String value;
    private String label;
}
