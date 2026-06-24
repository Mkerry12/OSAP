package com.mqq.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ResponseDetailVO implements Serializable {

    private Long id;
    private String respondent;
    private String submittedAt;
    private Integer duration;
    private List<AnswerDetailVO> answers;
}
