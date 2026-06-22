package com.mqq.vo;

import com.mqq.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDetailsVO extends SurveyVO implements Serializable {

    private String theme;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<QuestionVO> questionList;


}
