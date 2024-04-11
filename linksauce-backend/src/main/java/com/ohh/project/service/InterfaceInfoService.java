package com.ohh.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ohh.linksaucecommon.model.entity.InterfaceInfo;

/**
* @author 12994
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-03-29 18:05:35
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

}
