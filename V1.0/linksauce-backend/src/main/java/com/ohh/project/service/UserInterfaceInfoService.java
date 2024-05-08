package com.ohh.project.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ohh.linksaucecommon.model.entity.UserInterfaceInfo;

/**
* @author 12994
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2024-04-06 14:43:05
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
