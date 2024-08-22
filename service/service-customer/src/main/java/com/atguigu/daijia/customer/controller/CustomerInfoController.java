package com.atguigu.daijia.customer.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.service.CustomerInfoService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/customer/info")
public class CustomerInfoController {


	@Autowired
	private CustomerInfoService customerInfoService;

	@Operation(summary = "小程序授权登录")
	@GetMapping("/login/{code}")
	public Result<Long> login(@PathVariable String code) {
		//这里的code指的是二维码包含的信息
		return Result.ok(customerInfoService.login(code));
	}
	@Operation(summary = "获取客户登录信息")
	@GetMapping("/getCustomerLoginInfo/{customerId}")
	public Result<CustomerLoginVo> getCustomerLoginInfo(@PathVariable Long customerId) {
		return Result.ok(customerInfoService.getCustomerLoginInfo(customerId));
	}
	@Operation(summary = "获取客户基本信息")
	@GetMapping("/getCustomerInfo/{customerId}")
	public Result<CustomerInfo> getCustomerInfo(@PathVariable Long customerId) {
		return Result.ok(customerInfoService.getById(customerId));
	}
	@Operation(summary = "更新客户微信手机号码")
	@PostMapping("/updateWxPhoneNumber")
	public Result<Boolean> updateWxPhoneNumber(@RequestBody UpdateWxPhoneForm updateWxPhoneForm) {
		updateWxPhoneForm.setCustomerId(AuthContextHolder.getUserId());
		Db.lambdaUpdate(CustomerInfo.class)
				.eq(CustomerInfo::getId,updateWxPhoneForm.getCustomerId())
				.set(CustomerInfo::getPhone,AuthContextHolder.getUserId())
				.update();
		//return Result.ok(customerInfoService.updateWxPhoneNumber(updateWxPhoneForm));
		return Result.ok(true);
	}
}
