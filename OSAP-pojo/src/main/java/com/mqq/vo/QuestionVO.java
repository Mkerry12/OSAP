package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionVO {

    private Long id;

    private String type;

    private String title;

    private String required;

    private Integer sortOrder;

    List<QuestionOptionVO> options ;

}
