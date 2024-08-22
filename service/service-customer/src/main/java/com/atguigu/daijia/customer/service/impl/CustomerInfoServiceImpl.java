package com.atguigu.daijia.customer.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.atguigu.daijia.customer.config.WxConfigOperator;
import com.atguigu.daijia.customer.mapper.CustomerInfoMapper;
import com.atguigu.daijia.customer.mapper.CustomerLoginLogMapper;
import com.atguigu.daijia.customer.service.CustomerInfoService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.entity.customer.CustomerLoginLog;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {
    private final CustomerLoginLogMapper customerLoginLogMapper;
    private final WxMaService wxMaService;
    @Override
    public Long login(String code) {
        String openId=null;
        //获取code值，使用微信工具包对象获取唯一标识
        try {
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            openId=sessionInfo.getOpenid();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
        //根据openid 查询用户信息做登录
        CustomerInfo customerInfo = lambdaQuery().eq(CustomerInfo::getWxOpenId, openId).one();
        if(customerInfo==null){
            customerInfo=new CustomerInfo();
            customerInfo.setWxOpenId(openId);
            customerInfo.setNickname(String.valueOf((System.currentTimeMillis())));
            System.err.println(customerInfo.getNickname());
            customerInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
            save(customerInfo);
        }
        //记录日志信息,用的数据库进行存储
        CustomerLoginLog customerLoginLog=new CustomerLoginLog();
        System.err.println("customerInfo.getId()"+customerInfo.getId());
        customerLoginLog.setCustomerId(customerInfo.getId());
        customerLoginLog.setMsg("小程序登录");
        customerLoginLogMapper.insert(customerLoginLog);
        //返回用户id,我猜测时因为mybatisplus自动填充了
        return customerInfo.getId();
    }

    @Override
    public CustomerLoginVo getCustomerLoginInfo(Long customerId) {
        CustomerInfo customerInfo=lambdaQuery().eq(CustomerInfo::getId, customerId).one();
        CustomerLoginVo customerLoginVo = BeanUtil.copyProperties(customerInfo, CustomerLoginVo.class);
        Boolean isBindPhone = StringUtils.hasText(customerInfo.getPhone());
        //StringUtils.hasText(customerInfo.getPhone())==true? customerLoginVo.setIsBindPhone(true):customerLoginVo.setIsBindPhone(false);
        customerLoginVo.setIsBindPhone(isBindPhone);
        return customerLoginVo;
    }

    @Override
    public Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm) {
        // 调用微信 API 获取用户的手机号
        WxMaPhoneNumberInfo phoneInfo = null;
        try {
            phoneInfo = wxMaService.getUserService().getPhoneNoInfo(updateWxPhoneForm.getCode());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
        String phoneNumber = phoneInfo.getPhoneNumber();
        Long id = updateWxPhoneForm.getCustomerId();
        log.info("phoneInfo:{}", JSON.toJSONString(phoneInfo));
        return lambdaUpdate().eq(CustomerInfo::getId, id).set(CustomerInfo::getPhone, phoneNumber).update();
    }
}
