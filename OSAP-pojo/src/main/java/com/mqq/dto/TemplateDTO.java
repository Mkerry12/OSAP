package com.mqq.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TemplateDTO implements Serializable {

    private String title;

    private String description;

    private String category;

    private List<QuestionDTO> questions;
}
