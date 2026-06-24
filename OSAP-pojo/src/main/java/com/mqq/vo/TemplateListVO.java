package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateListVO implements Serializable {

    private Long id;

    private String title;

    private String category;

    private Integer questionCount;

    private Integer useCount;

    private LocalDateTime createAt;
}
