package com.mqq.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Survey implements Serializable {

    private Long id;

    private String title;

    private String description;

    private String type;//PUBLIC(公开) / ASSIGNED(指定用户)，默认 PUBLIC

    private List<String> targetPhones;//手机号数据，用于记录指定用户

    private String status;//筛选：DRAFT / PUBLISHED / CLOSED

    private Integer isAnonymous;//是否匿名，0不可，1可

    private Integer allowMultiSubmit;//是否允许重复提交

    private String theme;//主题样式

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer questionCount=0;

    private Integer responseCount=0;

    private Long creatorId;//与用户表关联

    private LocalDateTime createAt;

    private LocalDateTime updateAt;


}
