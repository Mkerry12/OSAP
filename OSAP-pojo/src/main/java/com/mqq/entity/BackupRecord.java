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
public class BackupRecord implements Serializable {

    private Long id;

    private String fileName;

    private Long fileSize;

    private Long creatorId;

    private LocalDateTime createAt;
}
