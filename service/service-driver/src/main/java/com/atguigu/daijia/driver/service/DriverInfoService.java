package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.entity.driver.DriverInfo;
import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DriverInfoService extends IService<DriverInfo> {

    Long login(String code);

    DriverLoginVo getDriverLoginInfo(Long driverId);

    Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm);

    DriverAuthInfoVo getDriverAuthInfo(Long driverId);

    Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm);
}
