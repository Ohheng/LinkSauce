package com.ohh.project.service.impl.inner;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ohh.linksaucecommon.model.entity.InterfaceInfo;
import com.ohh.linksaucecommon.service.InnerInterfaceInfoService;
import com.ohh.project.common.ErrorCode;
import com.ohh.project.exception.BusinessException;
import com.ohh.project.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StrUtil.hasBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<InterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(InterfaceInfo::getUrl, url).eq(InterfaceInfo::getMethod, method);
        return interfaceInfoMapper.selectOne(lambdaQueryWrapper);
    }
}
