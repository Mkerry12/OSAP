package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyPreviewVO implements Serializable {

    private Long surveyId;

    private String title;

    private String description;

    private Integer isAnonymous;

    private String status;

    private List<QuestionVO> questions;
}
