package com.atguigu.daijia.customer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "wx.miniapp")
@Component
@Data
public class WxConfigProperties {
    private String appId;
    private String secret;
}
