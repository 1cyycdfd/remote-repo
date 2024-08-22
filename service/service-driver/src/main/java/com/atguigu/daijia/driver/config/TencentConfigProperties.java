package com.atguigu.daijia.driver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "tencent.cloud")
public class TencentConfigProperties {
    private String secretId;
    private String secretKey;
    private String bucketPrivate;
    private String region;
    private String persionGroupId;
}
