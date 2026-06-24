package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyUpdateVO implements Serializable {

    private Long id;

    private String title;

    private LocalDateTime updateAt;
}
