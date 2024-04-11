package com.ohh.linksaucecommon.service;

import com.ohh.linksaucecommon.model.entity.InterfaceInfo;

/**
 * @author 12994
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2024-03-29 18:05:35
 */
public interface InnerInterfaceInfoService {

    /**
     * 数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     *
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

}
