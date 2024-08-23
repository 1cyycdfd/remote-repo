package com.atguigu.daijia.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.customer.service.OrderService;
import com.atguigu.daijia.map.client.MapFeignClient;
import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.form.customer.ExpectOrderForm;
import com.atguigu.daijia.model.form.customer.SubmitOrderForm;
import com.atguigu.daijia.model.form.map.CalculateDrivingLineForm;
import com.atguigu.daijia.model.form.order.OrderInfoForm;
import com.atguigu.daijia.model.form.rules.FeeRuleRequestForm;
import com.atguigu.daijia.model.vo.customer.ExpectOrderVo;
import com.atguigu.daijia.model.vo.map.DrivingLineVo;
import com.atguigu.daijia.model.vo.rules.FeeRuleResponseVo;
import com.atguigu.daijia.order.client.OrderInfoFeignClient;
import com.atguigu.daijia.rules.client.FeeRuleFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final MapFeignClient mapFeignClient;
    private final FeeRuleFeignClient feeRuleFeignClient;
    private final OrderInfoFeignClient orderInfoFeignClient;
    @Override
    public ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm) {
        //传递经纬度，通过地图获得距离，系统获得时间，通过规则获得费用
        CalculateDrivingLineForm calculateDrivingLineForm = BeanUtil.copyProperties(expectOrderForm, CalculateDrivingLineForm.class);
        //不进行if判断了
        DrivingLineVo data = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();
        FeeRuleRequestForm  feeRuleRequestForm =new FeeRuleRequestForm();
        feeRuleRequestForm.setDistance(data.getDistance());
        feeRuleRequestForm.setStartTime(new Date());
        //虽然写了等待时间的逻辑，但是没有却设定死了
        feeRuleRequestForm.setWaitMinute(0);
        FeeRuleResponseVo feeRuleResponseVo = feeRuleFeignClient.calculateOrderFee(feeRuleRequestForm).getData();
        ExpectOrderVo expectOrderVo = new ExpectOrderVo();
        expectOrderVo.setDrivingLineVo(data);
        expectOrderVo.setFeeRuleResponseVo(feeRuleResponseVo);
        return expectOrderVo;
    }

    @Override
    public Long submitOrder(SubmitOrderForm submitOrderForm) {
        CalculateDrivingLineForm calculateDrivingLineForm = BeanUtil.copyProperties(submitOrderForm, CalculateDrivingLineForm.class);
        DrivingLineVo data = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();
        FeeRuleRequestForm  feeRuleRequestForm =new FeeRuleRequestForm();
        feeRuleRequestForm.setDistance(data.getDistance());
        feeRuleRequestForm.setStartTime(new Date());
        //虽然写了等待时间的逻辑，但是没有却设定死了
        feeRuleRequestForm.setWaitMinute(0);
        FeeRuleResponseVo feeRuleResponseVo = feeRuleFeignClient.calculateOrderFee(feeRuleRequestForm).getData();

        OrderInfoForm orderInfoForm = BeanUtil.copyProperties(submitOrderForm, OrderInfoForm.class);
        //预估里程
        orderInfoForm.setExpectDistance(data.getDistance());
        orderInfoForm.setExpectAmount(feeRuleResponseVo.getTotalAmount());
        return orderInfoFeignClient.saveOrderInfo(orderInfoForm).getData();

    }
}
