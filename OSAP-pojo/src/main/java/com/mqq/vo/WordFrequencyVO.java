package com.mqq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordFrequencyVO implements Serializable {
    private String word;
    private Integer count;
}
