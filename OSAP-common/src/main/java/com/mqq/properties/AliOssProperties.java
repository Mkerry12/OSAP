package com.mqq.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mqq.alioss")
public class AliOssProperties {
    private String accessKeyId;
    private String endpoint;
    private String bucketName;
    private String accessKeySecret;
}
