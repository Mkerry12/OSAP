package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogVO implements Serializable {

    private Long id;

    private String type;

    private String operator;

    private String action;

    private String target;

    private String ip;

    private LocalDateTime createAt;
}
