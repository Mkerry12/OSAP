package com.mqq.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SubmitDTO implements Serializable {

    private List<AnswerSubmitDTO> answers;

    private String idempotencyKey;

    private Integer duration;
}
