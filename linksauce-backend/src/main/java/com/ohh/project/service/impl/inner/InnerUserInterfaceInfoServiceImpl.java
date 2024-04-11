package com.ohh.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ohh.linksaucecommon.model.entity.UserInterfaceInfo;
import com.ohh.linksaucecommon.service.InnerUserInterfaceInfoService;
import com.ohh.project.common.ErrorCode;
import com.ohh.project.exception.BusinessException;
import com.ohh.project.mapper.UserInterfaceInfoMapper;
import com.ohh.project.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }
    //
    // @Override
    // public boolean hasInvokeNum(long userId, long interfaceInfoId) {
    //     if (userId <= 0 || interfaceInfoId <= 0) {
    //         throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
    //     }
    //
    //     LambdaQueryWrapper<UserInterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
    //     queryWrapper.eq(UserInterfaceInfo::getUserId, userId)
    //             .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId)
    //             .gt(UserInterfaceInfo::getLeftNum, 0);
    //
    //     UserInterfaceInfo userInterfaceInfo = userInterfaceInfoMapper.selectOne(queryWrapper);
    //     return userInterfaceInfo != null;
    // }
    //
    // @Override
    // public boolean invokeInterfaceCount(long userId, long interfaceInfoId) {
    //     if (userId <= 0 || interfaceInfoId <= 0) {
    //         throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
    //     }
    //
    //     LambdaUpdateWrapper<UserInterfaceInfo> updateWrapper = new LambdaUpdateWrapper<>();
    //     updateWrapper.eq(UserInterfaceInfo::getUserId, userId)
    //             .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId)
    //             .gt(UserInterfaceInfo::getLeftNum, 0)
    //             .setSql("left_num = left_num -1, total_num = total_num + 1");
    //
    //     int updateCount = userInterfaceInfoMapper.update(null, updateWrapper);
    //     return updateCount > 0;
    // }

}
