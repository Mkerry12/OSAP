package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackupVO implements Serializable {

    private Long id;

    private String fileName;

    private Long fileSize;

    private LocalDateTime createAt;
}
