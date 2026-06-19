package com.mqq.config;

import com.mqq.properties.AliOssProperties;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OssConfiguration {

    private final AliOssProperties aliOssProperties;

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret()
        );
    }
}
