package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.driver.client.CosFeignClient;
import com.atguigu.daijia.driver.service.CosService;
import com.atguigu.daijia.model.vo.driver.CosUploadVo;
import com.qcloud.cos.COSClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class CosServiceImpl implements CosService {

    private final CosFeignClient cosFeignClient;
    @Override
    public CosUploadVo upload(MultipartFile file, String path) {
        return cosFeignClient.upload(file, path).getData();
    }
}
