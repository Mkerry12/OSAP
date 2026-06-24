package com.mqq.vo;

import com.mqq.entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyVO implements Serializable {

    private Long id;

    private String title;

    private String description;

    private String type;

    private Integer status;

    private Boolean isAnonymous;

    private Boolean allowMultiSubmit;

    private Integer questionCount;

    private Integer responseCount;

    private UserInfo creator;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}
