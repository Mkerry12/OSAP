package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserVO implements Serializable {

    private Long id;

    private String username;

    private String phone;

    private String email;

    private String role;

    private Integer status;

    private Integer surveyCount;

    private LocalDateTime createAt;
}
