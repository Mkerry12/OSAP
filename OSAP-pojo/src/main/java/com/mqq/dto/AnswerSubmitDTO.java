package com.mqq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AnswerSubmitDTO implements Serializable {

    private Long questionId;

    private String value;
}
