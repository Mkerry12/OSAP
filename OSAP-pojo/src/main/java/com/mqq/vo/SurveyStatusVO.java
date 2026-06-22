package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyStatusVO implements Serializable {

    private Long id;

    private String status;

    private LocalDateTime updatedTime;
}
