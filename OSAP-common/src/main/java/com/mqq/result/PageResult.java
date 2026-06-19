package com.mqq.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 封装页面结果，page和size是基于前端数据的再返回
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult implements Serializable {

    private Integer page;

    private Integer size;

    private Integer total;

    private List records;

}
