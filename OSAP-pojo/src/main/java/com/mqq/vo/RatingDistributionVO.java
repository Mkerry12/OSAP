package com.mqq.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RatingDistributionVO implements Serializable {
    private Integer score;
    private Integer count;
    private Double percentage;
}
