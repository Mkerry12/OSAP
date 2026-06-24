package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSurveyVO implements Serializable {

    private Long id;

    private String title;

    private String status;

    private String type;

    private Integer questionCount;

    private Integer responseCount;

    private CreatorVO creator;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}
