package com.mqq.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DailyResponseVO implements Serializable {
    private String date;
    private Integer count;
}
