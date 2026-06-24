package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyFillVO implements Serializable {

    private Long surveyId;

    private String title;

    private String description;

    private Boolean isAnonymous;

    private Boolean submitted;

    private List<QuestionVO> questions;

}
