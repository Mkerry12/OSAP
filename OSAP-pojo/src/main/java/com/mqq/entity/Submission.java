package com.mqq.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long surveyId;

    private Long userId;

    private String idempotencyKey;

    private Integer duration;

    private LocalDateTime submitAt;
}
