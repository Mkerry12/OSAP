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
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private Integer status;

    private String phone;

    private String email;

    private String password = "";

    private String image;

    private String role;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private Integer surveyCount = 0;

    private Integer responseCount = 0;
}
