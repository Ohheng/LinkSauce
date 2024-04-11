package com.ohh.project.service.impl.inner;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ohh.linksaucecommon.model.entity.User;
import com.ohh.linksaucecommon.service.InnerUserService;
import com.ohh.project.common.ErrorCode;
import com.ohh.project.exception.BusinessException;
import com.ohh.project.mapper.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        if (StrUtil.isBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getAccessKey, accessKey);
        return userMapper.selectOne(lambdaQueryWrapper);
    }
}
