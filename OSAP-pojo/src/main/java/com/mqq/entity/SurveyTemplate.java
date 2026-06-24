package com.mqq.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyTemplate implements Serializable {

    private Long id;

    private String title;

    private String description;

    private String category;

    private String questions;

    private Integer questionCount;

    private Integer useCount;

    private Long creatorId;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}
