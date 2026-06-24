package com.mqq.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog implements Serializable {

    private Long id;

    private String type;

    private String operator;

    private String action;

    private String target;

    private String ip;

    private String detail;

    private LocalDateTime createAt;
}
