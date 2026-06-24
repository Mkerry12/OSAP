package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyCopyVO implements Serializable {

    private Long id;

    private String title;

    private String status;

    private Integer questionCount;

    private Integer responseCount;

    private LocalDateTime createAt;
}
