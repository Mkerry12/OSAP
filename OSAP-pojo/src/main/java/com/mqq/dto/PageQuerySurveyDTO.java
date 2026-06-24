package com.mqq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQuerySurveyDTO implements Serializable {

    private Integer page;

    private Integer size;

    private String status;

    private String keyword;

    private String sortBy;

    private String sortOrder;

    private Long createId;
}
