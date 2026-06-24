package com.mqq.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long surveyId;

    private String type;

    private String title;

    private Boolean required;

    private Integer sortOrder;

    private Integer minRating;

    private Integer maxRating;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private List<QuestionOption> options;
}
