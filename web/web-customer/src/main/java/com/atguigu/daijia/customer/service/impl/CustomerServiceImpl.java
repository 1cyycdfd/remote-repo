package com.atguigu.daijia.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.client.CustomerInfoFeignClient;
import com.atguigu.daijia.customer.service.CustomerService;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final RedisTemplate redisTemplate;
    private final CustomerInfoFeignClient customerInfoFeignClient;
    public String login(String code) {
        //因为是远程调用，所以可能会出现问题
        //用code进行远程调用获得用户id
        Result<Long> login = customerInfoFeignClient.login(code);
        if (login.getCode() != 200) {
           throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        Long customerId = login.getData();
        if(customerId==null){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        //根据用户id生成jwt令牌,但是为了服务安全，令牌不能直接存储在redis中，需要加密且不能直接暴露 ，且用token
        String token= UUID.randomUUID().toString().replaceAll("-","");
                //string类型有个方法replace方法能替换字符串中的内容
        //且存储时为了区分需要uuid加上前缀存储再redis中设置过期时间
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX+token,customerId.toString(),30*60, TimeUnit.SECONDS);
        //返回令token，token存储在redis中，且设置过期时间,作用是为了防止token被泄露，不能直接存储在redis中
        return token;
    }

    @Override
    public CustomerLoginVo getCustomerLoginInfo(String token) {
        String ID=(String) redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);
        //如果数据有异常应该是立刻判断,因为多写一点，导致错误的原因就会多一点
        if(StringUtils.isNotEmpty(ID)){
            Long  id = Long.parseLong(ID);
            Result<CustomerLoginVo> customerLoginInfo = customerInfoFeignClient.getCustomerLoginInfo(id);
            if(customerLoginInfo.getCode()!=200){
                throw new GuiguException(ResultCodeEnum.DATA_ERROR);
            }//Char
            CustomerLoginVo data = customerLoginInfo.getData();
            if(data!=null){
                return data;
            }
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);

        }
        throw new GuiguException(ResultCodeEnum.DATA_ERROR);
    }

    @Override
    public CustomerLoginVo getLoginInfo() {
        return null;
    }

    @Override
    public CustomerLoginVo getCustomerLoginInfo() {
        Long userId = AuthContextHolder.getUserId();
        System.out.println("userId:"+userId);;
        Result<CustomerLoginVo> customerLoginInfo = customerInfoFeignClient.getCustomerLoginInfo(userId);
        if(customerLoginInfo.getCode()!=200){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }//Char
        CustomerLoginVo data = customerLoginInfo.getData();
        if(data!=null){
            return data;
        }
        throw new GuiguException(ResultCodeEnum.DATA_ERROR);
    }

    @Override
    public boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm) {
        Result<Boolean> booleanResult=customerInfoFeignClient.updateWxPhoneNumber(updateWxPhoneForm);
        return true;
    }
}
