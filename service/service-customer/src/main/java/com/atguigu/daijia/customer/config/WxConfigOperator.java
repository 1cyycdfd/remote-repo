package com.atguigu.daijia.customer.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@RequiredArgsConstructor
@Configuration
public class WxConfigOperator {
    private final WxConfigProperties wxConfigProperties;
    @Bean
    public WxMaService wxConfigOperatoe() {
        //配置wx的实体类&Bean
        WxMaDefaultConfigImpl wxMaDefaultConfig = new WxMaDefaultConfigImpl();
        wxMaDefaultConfig.setAppid(wxConfigProperties.getAppId());
        wxMaDefaultConfig.setSecret(wxConfigProperties.getSecret());
        //WxMaService.builder().config(wxMaDefaultConfig).build();

        WxMaService service=new WxMaServiceImpl();
        service.setWxMaConfig(wxMaDefaultConfig);
        return service;
    }
}
