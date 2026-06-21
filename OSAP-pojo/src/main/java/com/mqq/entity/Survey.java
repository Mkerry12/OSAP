package com.mqq.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Survey implements Serializable {

    private Integer id;

    private String title;

    private String description;

    private String type;

    private List<String> targetPhones;//手机号数据，用于记录指定用户

    private String status;

    private Integer isAnonymous;//是否匿名，0不可，1可

    private Integer allowMultiSubmit;//是否允许重复提交

    private String theme;

    private LocalDateTime StartTime;

    private LocalDateTime EndTime;

    private Integer questionCount;

    private Integer responseCount;

    private Long creatorId;//与用户表关联

    private LocalDateTime creationTime;

    private LocalDateTime updateTime;


}
