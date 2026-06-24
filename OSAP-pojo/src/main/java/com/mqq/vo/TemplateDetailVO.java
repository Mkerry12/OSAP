package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDetailVO implements Serializable {

    private Long id;

    private String title;

    private String description;

    private String category;

    private Integer questionCount;

    private Integer useCount;

    private CreatorVO creator;

    private List<QuestionVO> questions;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}
