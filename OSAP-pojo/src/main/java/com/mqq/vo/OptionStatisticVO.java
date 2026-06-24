package com.mqq.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OptionStatisticVO implements Serializable {
    private String label;
    private Integer count;
    private Double percentage;
}
