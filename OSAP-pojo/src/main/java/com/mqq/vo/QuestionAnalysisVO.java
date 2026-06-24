package com.mqq.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionAnalysisVO implements Serializable {
    private List<QuestionAnalysisItemVO> questions;
}
