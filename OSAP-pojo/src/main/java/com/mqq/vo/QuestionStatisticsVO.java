package com.mqq.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionStatisticsVO implements Serializable {
    private String type;
    private List<OptionStatisticVO> options;
    private Double averageScore;
    private Integer maxScore;
    private Integer minScore;
    private List<RatingDistributionVO> distribution;
    private String wordCloud;
}
