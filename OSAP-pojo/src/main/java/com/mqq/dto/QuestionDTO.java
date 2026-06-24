package com.mqq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO implements Serializable {

    private String type;

    private String title;

    private Boolean required;

    private Integer sortOrder;

    private List<QuestionOptionDTO> options;

    private Integer minRating;

    private Integer maxRating;

}
