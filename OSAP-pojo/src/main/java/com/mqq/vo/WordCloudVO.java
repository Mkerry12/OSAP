package com.mqq.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WordCloudVO implements Serializable {
    private List<WordFrequencyVO> words;
}
