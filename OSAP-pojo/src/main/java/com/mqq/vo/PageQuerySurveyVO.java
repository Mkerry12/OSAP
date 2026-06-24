package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQuerySurveyVO implements Serializable {

    private Long id;

    private String title;

    private String status;

    private String type;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}
